package boot.imvc.servlet.data;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import boot.imvc.servlet.data.bean.Item;
import boot.imvc.servlet.data.bean.Root;
import boot.imvc.servlet.data.dialect.Dialect;
import boot.imvc.servlet.data.dialect.GBaseDialect;
import boot.imvc.servlet.data.dialect.H2Dialect;
import boot.imvc.servlet.data.dialect.MysqlDialect;
import boot.imvc.servlet.data.dialect.OracleDialect;
import boot.imvc.servlet.data.dialect.PostgreSqlDialect;
import boot.imvc.servlet.data.dialect.Sqlite3Dialect;
import boot.imvc.servlet.utils.MD5Util;
import boot.imvc.servlet.utils.TypeUtil;

/**
 * 数据库与连接池管理
 */
public class DataSource {

	/**
	 * 初始化连接池
	 * 
	 * @param props
	 * @param show_sql
	 * @return
	 */
	public final static Map<String, Executor> initDataSource(Properties prop) {
		Map<String, Executor> results = new HashMap<String, Executor>();
		try {
			// 解析的sql
			List<Root> roots = new ArrayList<Root>();
			URL resource = Thread.currentThread().getContextClassLoader().getResource((String) prop.get("mapper.scan"));
			read(resource.getFile(), roots);
			Map<String, List<Item>> mappers = loadMapper(roots);

			// 数据源配置解析
			String ds = (String) prop.get("datasource.names");
			String[] dss = StringUtils.split(ds, ",");
			for (String d : dss) {
				Properties cpProps = new Properties();
				for (Object key : prop.keySet()) {
					String skey = (String) key;
					if (skey.startsWith(d + ".")) {
						String name = skey.substring(d.length() + 1);
						cpProps.put(name, prop.get(skey));
					}
				}
				// 初始化数据源-hikari
				HikariConfig conf = new HikariConfig();
				copyProperties(conf, cpProps);
				HikariDataSource dataSource = new HikariDataSource(conf);
				Connection conn = dataSource.getConnection();
				DbUtils.closeQuietly(conn);
				// 加载属于当前数据源的执行SQL
				Map<String, Item> sqlMap = initMapper(mappers.get(d));
				// 数据库方言
				String driverClass = (String) prop.get(d + ".driverClassName");
				results.put(MD5Util.lower32(d), new Executor(d,dataSource, dialect(driverClass), sqlMap));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	// 将item实例对象转为map
	private static Map<String, Item> initMapper(List<Item> list) {
		Map<String, Item> sql = new HashMap<String, Item>();
		if (list != null && list.size() > 0) {
			for (Item item : list) {
				sql.put(item.getId(), item);
			}
		}
		return sql;
	}

	private static Map<String, List<Item>> loadMapper(List<Root> roots) {
		Map<String, List<Item>> result = new HashMap<String, List<Item>>();
		// 数据源汇总
		Set<String> db = new HashSet<String>();
		List<Item> allItem = new ArrayList<Item>();
		if (roots.size() > 0) {
			for (Root root : roots) {
				List<Item> items = root.getItems();
				allItem.addAll(items);
				if (items.size() > 0) {
					for (Item item : items) {
						db.add(item.getSource());
					}
				}
			}
		}
		if (allItem.size() > 0) {
			// 判断所有Item对象ID是否有重复
			List<String> repeat = allItem.stream().collect(Collectors.groupingBy(Item::getId, Collectors.counting())).entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
			if (repeat.size() > 0) {
				throw new RuntimeException("Multiple sql id defined - " + repeat.toString());
			}

			for (String ds : db) {
				List<Item> items = new ArrayList<Item>();
				for (Item item : allItem) {
					if (StringUtils.equals(ds, item.getSource())) {
						items.add(item);
					}
				}
				result.put(ds, items);
			}
		}
		return result;
	}

	private static void read(String scanPackage, List<Root> roots) throws Exception {
		File pack = new File(scanPackage);
		if (pack.exists()) {
			for (File file : pack.listFiles()) {
				if (file.isDirectory()) {
					read(file.getAbsolutePath(), roots);
				} else {
					SAXReader sax = new SAXReader();// 创建一个SAXReader对象
					Document document = sax.read(file);// 获取document对象,如果文档无节点，则会抛出Exception提前结束
					Element rootElement = document.getRootElement();// 获取根节点
					Root root = new Root();
					List<Item> items = new ArrayList<Item>();
					String rootSource = rootElement.attributeValue("source");
					root.setSource(rootSource);
					@SuppressWarnings("unchecked")
					List<Element> elements = rootElement.elements();
					if (elements.size() > 0) {
						for (Element element : elements) {
							Item item = new Item();
							item.setId(element.attributeValue("id"));
							String elementSource = element.attributeValue("source");
							elementSource = StringUtils.isEmpty(elementSource) ? rootSource : elementSource;
							elementSource = StringUtils.isEmpty(elementSource) ? Constant.DEFAULT_DATASOURCE : elementSource;
							item.setSource(elementSource);
							item.setValue(element.getStringValue().trim());
							items.add(item);
						}
					}
					root.setItems(items);
					roots.add(root);
				}
			}
		}
	}

	private static void copyProperties(HikariConfig dest, Properties src) {
		Method[] methods = dest.getClass().getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("set") == false || methodName.length() <= 3) {
				continue;
			}
			Class<?>[] types = method.getParameterTypes();
			if (types.length != 1) {
				continue;
			}

			String attrName = StringUtils.uncapitalize(methodName.substring(3));
			if (src.containsKey(attrName)) {
				try {
					String paraValue = src.getProperty(attrName);
					Object value = paraValue != null ? TypeUtil.cast(types[0], paraValue) : null;
					method.invoke(dest, value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private static Dialect dialect(String driver) {
		Dialect dialect = new PostgreSqlDialect();
		if (driver.contains("postgresql") || driver.contains("GreenplumDriver")) {
			dialect = new PostgreSqlDialect();
		} else if (driver.contains("oracle")) {
			dialect = new OracleDialect();
		} else if (driver.contains("mysql")) {
			dialect = new MysqlDialect();
		} else if (driver.contains("sqlite")) {
			dialect = new Sqlite3Dialect();
		} else if (driver.contains("h2")) {
			dialect = new H2Dialect();
		} else if (driver.contains("gbase")) {
			dialect = new GBaseDialect();
		}
		return dialect;
	}
}
