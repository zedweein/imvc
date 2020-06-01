package boot.imvc.servlet.data;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;
import boot.imvc.servlet.cache.CacheManager;
import boot.imvc.servlet.cache.Key;
import boot.imvc.servlet.data.bean.Item;
import boot.imvc.servlet.data.bean.Page;
import boot.imvc.servlet.data.bean.Record;
import boot.imvc.servlet.data.dialect.Dialect;
import boot.imvc.servlet.data.handler.IntegerHandler;
import boot.imvc.servlet.data.handler.LongHandler;
import boot.imvc.servlet.data.handler.RecordHandler;
import boot.imvc.servlet.data.handler.RecordListHandler;
import oracle.jdbc.OracleTypes;

/**
 * 数据库操作类，实现所有数据库的操作
 * 
 * @author admin
 */

@SuppressWarnings("unchecked")
public class Executor {

	private static final Logger log = Logger.getLogger(Executor.class);
	private final QueryRunner runner = new QueryRunner();
	private DataSource dataSource;
	private Dialect dialect;
	private String sourceName;
	private Map<String, Item> sqls = new HashMap<String, Item>();

	public Executor(String sourceName, DataSource dataSource, Dialect dialect, Map<String, Item> sqls) {
		this.sourceName = sourceName;
		this.dataSource = dataSource;
		this.dialect = dialect;
		this.sqls = sqls;
	}

	// 查询当前数据源的sql-id索引
	public String sql(String id) {
		Item item = sqls.get(id);
		if (item == null) {
			throw new RuntimeException("SQL not found - [" + id + "@" + sourceName + "]");
		}
		return item.getValue();
	}

	// 查询当前数据源的sql-id索引
	public String sql(String id, Map<String, Object> param) {
		Item item = sqls.get(id);
		if (item == null) {
			throw new RuntimeException("SQL not found - [" + id + "@" + sourceName + "]");
		}
		return item.parse(param);
	}

	public String name() {
		return sourceName;
	}

	public Connection getConn() throws SQLException {
		return dataSource.getConnection();
	}

	// 单个对象，bean,封装对象 record和hashMap对象查询
	public <T> T bean(Class<T> beanClass, String sql, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql, params);
			return (T) runner.query(conn, sql, _IsPrimitive(beanClass) ? new ScalarHandler<T>() : new BeanHandler<T>(beanClass), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public <T> T beanCache(Class<T> beanClass, String region, String sql, Object... params) {
		Key key = new Key(sql, params);
		T bean = (T) CacheManager.get(region, key);
		if (Objects.isNull(bean)) {
			bean = bean(beanClass, sql, params);
			CacheManager.set(region, key, (Serializable) bean);
		}
		return bean;
	}

	public Record record(String sql, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql, params);
			return runner.query(conn, sql, new RecordHandler(), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public Record recordCache(String region, String sql, Object... params) {
		Key key = new Key(sql, params);
		Record rd = (Record) CacheManager.get(region, key);
		if (Objects.isNull(rd)) {
			rd = record(sql, params);
			CacheManager.set(region, key, rd);
		}
		return rd;
	}

	public Map<String, Object> map(String sql, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql, params);
			return runner.query(conn, sql, new MapHandler(), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public Map<String, Object> mapCache(String region, String sql, Object... params) {
		Key key = new Key(sql, params);
		Map<String, Object> fst = (Map<String, Object>) CacheManager.get(region, key);
		if (Objects.isNull(fst) || fst.isEmpty()) {
			fst = map(sql, params);
			CacheManager.set(region, key, (Serializable) fst);
		}
		return fst;
	}

	// 对象，bean,封装对象 record和hashMap对象查询List
	public <T> List<T> beanList(Class<T> beanClass, String sql, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql, params);
			return (List<T>) runner.query(conn, sql, _IsPrimitive(beanClass) ? new ColumnListHandler<T>() : new BeanListHandler<T>(beanClass), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public <T> List<T> beanListCache(Class<T> beanClass, String region, String sql, Object... params) {
		Key key = new Key(sql, params);
		List<T> list = (List<T>) CacheManager.get(region, key);
		if (Objects.isNull(list) || list.isEmpty()) {
			list = beanList(beanClass, sql, params);
			CacheManager.set(region, key, (Serializable) list);
		}
		return list;
	}

	public List<Record> recordList(String sql, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql, params);
			return runner.query(conn, sql, new RecordListHandler(), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public List<Record> recordListCache(String region, String sql, Object... params) {
		Key key = new Key(sql, params);
		List<Record> list = (List<Record>) CacheManager.get(region, key);
		if (Objects.isNull(list) || list.isEmpty()) {
			list = recordList(sql, params);
			CacheManager.set(region, key, (Serializable) list);
		}
		return list;
	}

	public List<Map<String, Object>> mapList(String sql, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql, params);
			return runner.query(conn, sql, new MapListHandler(), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public List<Map<String, Object>> mapListCache(String region, String sql, Object... params) {
		Key key = new Key(sql, params);
		List<Map<String, Object>> list = (List<Map<String, Object>>) CacheManager.get(region, key);
		if (Objects.isNull(list) || list.isEmpty()) {
			list = mapList(sql, params);
			CacheManager.set(region, key, (Serializable) list);
		}
		return list;
	}

	// 对象，bean,封装对象 record和hashMap对象查询分页
	public <T> Page<T> beanPage(Class<T> beanClass, String sql, Page<T> page, Object... params) {
		return page(beanClass, sql, page.getStart(), page.getLength(), 0, params);
	}

	public <T> Page<T> beanPageCache(Class<T> beanClass, String region, String sql, Page<T> page, Object... params) {
		Key key = new Key(sql, params, page.getStart(), page.getLength());
		Page<T> pg = (Page<T>) CacheManager.get(region, key);
		if (Objects.isNull(pg)) {
			pg = page(beanClass, sql, page.getStart(), page.getLength(), 0, params);
			CacheManager.set(region, key, pg);
		}
		return pg;
	}

	public Page<Record> recordPage(String sql, Page<?> page, Object... params) {
		return page(sql, page.getStart(), page.getLength(), page.getRecordsTotal(), params);
	}

	public Page<Record> recordPageCache(String region, String sql, Page<?> page, Object... params) {
		Key key = new Key(sql, params, page.getStart(), page.getLength());
		Page<Record> pg = (Page<Record>) CacheManager.get(region, key);
		if (Objects.isNull(pg) || pg.getRecordsTotal() < 1) {
			pg = page(sql, page.getStart(), page.getLength(), page.getRecordsTotal(), params);
			CacheManager.set(region, key, pg);
		}
		return pg;
	}

	public Page<Map<String, Object>> mapPage(String sql, Page<?> page, Object... params) {
		return mapPage(sql, page.getStart(), page.getLength(), page.getRecordsTotal(), params);
	}

	public Page<Map<String, Object>> mapPageCache(String region, String sql, Page<?> page, Object... params) {
		Key key = new Key(sql, params, page.getStart(), page.getLength());
		Page<Map<String, Object>> pg = (Page<Map<String, Object>>) CacheManager.get(region, key);
		if (Objects.isNull(pg) || pg.getRecordsTotal() < 1) {
			pg = mapPage(sql, page.getStart(), page.getLength(), page.getRecordsTotal(), params);
			CacheManager.set(region, key, pg);
		}
		return pg;
	}

	// 查询TOP
	public <T> List<T> topBean(Class<T> beanClass, String sql, Integer top, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			String sqlList = dialect.forPage(0, top, sql);
			showSql(sqlList, params);
			return (List<T>) runner.query(conn, sqlList, _IsPrimitive(beanClass) ? new ColumnListHandler<T>() : new BeanListHandler<T>(beanClass), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public List<Record> recordTop(String sql, Integer top, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			String sqlList = dialect.forPage(0, top, sql);
			showSql(sqlList, params);
			List<Record> list = runner.query(conn, sqlList, new RecordListHandler(0), params);
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public List<Map<String, Object>> mapTop(String sql, Integer top, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql, params);
			return runner.query(conn, sql, new MapListHandler(), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	// 存储过程调用
	public int proc(String sql, Object... params) {
		Connection conn = null;
		try {
			conn = getConn();
			showSql(sql);
			return runner.update(conn, sql, params);
		} catch (SQLException e) {
			rollback(conn);
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public List<Record> procList(String sql, Object... params) {
		if (dialect.isOracle()) {
			return procOraList(sql, params);
		} else {
			return procPgList(sql, params);
		}
	}

	public List<Record> procListCache(String region, String sql, Object... params) {
		Key key = new Key(sql, params);
		List<Record> list = (List<Record>) CacheManager.get(region, key);
		if (Objects.isNull(list) && list.isEmpty()) {
			list = procList(sql, params);
			CacheManager.set(region, key, (Serializable) list);
		}
		return list;
	}

	// 增删改
	public int insert(String sql, Object... params) {
		Connection conn = null;
		Boolean autoCommit = null;
		try {
			conn = getConn();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			showSql(sql, params);
			int rst = runner.insert(conn, sql, new IntegerHandler(), params);
			conn.commit();
			return rst;
		} catch (SQLException e) {
			rollback(conn);
		} finally {
			close(conn, autoCommit);
		}
		return 0;
	}

	public int insert(String tableName, Record record) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		dialect.forDbSave(tableName, record, sql, params);
		showSql(sql.toString(), params.toArray());
		return insert(sql.toString(), params.toArray());
	}

	public int[] batchInsert(String tableName, List<Record> recordList) {
		if (recordList == null || recordList.size() == 0) {
			return new int[0];
		}
		Record record = recordList.get(0);
		Map<String, Object> cols = record.getColumns();
		String[] colNames = new String[cols.entrySet().size()];
		int index = 0;
		// the same as the iterator in Dialect.forDbSave() to ensure the order
		// of the columns
		for (Entry<String, Object> e : cols.entrySet()) {
			colNames[index++] = e.getKey();
		}
		StringBuilder sql = new StringBuilder();
		List<Object> parasNoUse = new ArrayList<Object>();
		dialect.forDbSave(tableName, record, sql, parasNoUse);
		int size = recordList.size();
		int colSize = colNames.length;
		Object[][] params = new Object[size][colSize];
		for (int i = 0; i < size; i++) {
			Record rd = recordList.get(i);
			for (int j = 0; j < colSize; j++) {
				Object value = rd.get(colNames[j]);
				params[i][j] = value;
			}
		}
		return batch(sql.toString(), params);
	}

	public int[] batch(String sql, Object[][] params) {
		Connection conn = null;
		Boolean autoCommit = null;
		try {
			conn = getConn();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			showSql(sql);
			int[] rst = runner.batch(conn, sql, params);
			conn.commit();
			return rst;
		} catch (SQLException e) {
			rollback(conn);
			throw new RuntimeException(e);
		} finally {
			close(conn, autoCommit);
		}
	}

	public int update(String sql, Object... params) {
		Connection conn = null;
		Boolean autoCommit = null;
		try {
			conn = getConn();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			showSql(sql, params);
			int rst = runner.update(conn, sql, params);
			conn.commit();
			return rst;
		} catch (SQLException e) {
			log.error("update failed:" + e);
			rollback(conn);
			throw new RuntimeException(e);
		} finally {
			close(conn, autoCommit);
		}
	}

	public int update(String tableName, String primaryKey, Record record) {
		String[] pKeys = primaryKey.split(",");
		Map<String, Object> keyMap = new HashMap<String, Object>();
		for (int i = 0; i < pKeys.length; i++) {
			Object value = record.get(pKeys[i].trim());
			if (null != value) {
				keyMap.put(pKeys[i], value);
			}
		}

		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		dialect.forDbUpdate(tableName, keyMap, record, sql, params);

		if (params.size() <= 1) { // Needn't update
			return -1;
		}
		return update(sql.toString(), params.toArray());
	}

	public int[] batch(List<String> sqlList) {
		Connection conn = null;
		Statement st = null;
		Boolean autoCommit = null;
		try {
			if (sqlList == null || sqlList.size() == 0) {
				return new int[0];
			}
			conn = getConn();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			st = conn.createStatement();
			for (String sql : sqlList) {
				showSql(sql);
				st.addBatch(sql);
			}
			int[] result = st.executeBatch();
			conn.commit();
			return result;
		} catch (SQLException e) {
			log.error("batch failed:" + e);
			rollback(conn);
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(st);
			close(conn, autoCommit);
		}
	}

	public int delete(String sql, Object... params) {
		return this.update(sql, params);
	}

	// 封装私有方法
	private Page<Record> page(String sql, int start, int length, int total, Object... params) {
		if (start < 0 || length < 0) {
			throw new IllegalArgumentException("Illegal parameter of 'pageNo' or 'pageSize', Must be positive.");
		}
		Connection conn = null;
		try {
			conn = getConn();
			long totalRow = total;
			if (total == 0) {
				String countSql = "select count(1) from (" + sql + ") t";
				showSql(countSql, params);
				totalRow = runner.query(conn, countSql, new LongHandler(), params);
			}
			if (totalRow == 0) {
				return new Page<Record>(new ArrayList<Record>(0), start, length, 0);
			}
			String sqlList = dialect.forPage(start, length, sql);
			showSql(sqlList, params);
			List<Record> list = runner.query(conn, sqlList, new RecordListHandler(), params);
			return new Page<Record>(list, start, length, (int) totalRow);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	private <T> Page<T> page(Class<T> beanClass, String sql, int start, int length, int total, Object... params) {
		if (start < 0 || length < 0) {
			throw new IllegalArgumentException("Illegal parameter of 'start' or 'length', Must be positive.");
		}
		Connection conn = null;
		try {
			conn = getConn();
			long totalRow = total;
			if (total == 0) {
				String countSql = "select count(1) from (" + sql + ") t";
				showSql(countSql, params);
				totalRow = runner.query(conn, countSql, new LongHandler(), params);
			}
			if (totalRow == 0) {
				return new Page<T>(new ArrayList<T>(0), start, length, 0);
			}
			// --------
			String sqlList = dialect.forPage(start, length, sql);
			showSql(sqlList, params);
			List<T> list = runner.query(conn, sqlList, _IsPrimitive(beanClass) ? new ColumnListHandler<T>() : new BeanListHandler<T>(beanClass), params);
			return new Page<T>(list, start, length, (int) totalRow);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	private Page<Map<String, Object>> mapPage(String sql, int start, int length, int total, Object... params) {
		if (start < 0 || length < 0) {
			throw new IllegalArgumentException("Illegal parameter of 'pageNo' or 'pageSize', Must be positive.");
		}
		Connection conn = null;
		try {
			conn = getConn();
			long totalRow = total;
			if (total == 0) {
				String countSql = "select count(1) from (" + sql + ") t";
				showSql(countSql, params);
				totalRow = runner.query(conn, countSql, new LongHandler(), params);
			}
			if (totalRow == 0) {
				return new Page<Map<String, Object>>(new ArrayList<Map<String, Object>>(0), start, length, 0);
			}

			String sqlList = dialect.forPage(start, length, sql);
			showSql(sqlList, params);
			List<Map<String, Object>> list = runner.query(conn, sqlList, new MapListHandler(), params);
			return new Page<Map<String, Object>>(list, start, length, (int) totalRow);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	private List<Record> procPgList(String sql, Object... params) {
		showSql(sql, params);
		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		Boolean autoCommit = null;
		List<Record> results = new ArrayList<Record>();
		try {
			conn = getConn();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			showSql(sql, params);
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, Types.OTHER);
			int num = 2;
			for (Object o : params) {
				stmt.setObject(num, o);
				num++;
			}
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
			RecordListHandler rsh = new RecordListHandler();
			results = rsh.handle(rs);
			conn.commit();
		} catch (SQLException e) {
			rollback(conn);
			log.error("procList store produce to List<Record> failed:" + e);
			throw new RuntimeException(e);
		} finally {
			close(stmt, rs);
			close(conn, autoCommit);
		}
		return results;
	}

	private List<Record> procOraList(String sql, Object... params) {
		showSql(sql, params);
		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		List<Record> results = new ArrayList<Record>();
		try {
			conn = getConn();
			showSql(sql, params);
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			int num = 2;
			for (Object o : params) {
				stmt.setObject(num, o);
				num++;
			}
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
			RecordListHandler rsh = new RecordListHandler();
			results = rsh.handle(rs);
		} catch (SQLException e) {
			rollback(conn);
			throw new RuntimeException(e);
		} finally {
			close(conn, stmt, rs);
		}
		return results;
	}

	private void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("close Connection failed:" + e.getMessage(), e);
			}
		}
	}

	private void close(Connection conn, Boolean autoCommit) {
		if (conn != null) {
			try {
				if (autoCommit != null) {
					conn.setAutoCommit(autoCommit);
				}
				conn.close();
			} catch (SQLException e) {
				log.error("close set Connection autoCommit and close failed:" + e.getMessage(), e);
			}
		}
	}

	private void close(Connection conn, Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				log.error("close Statement failed:" + e.getMessage(), e);
			}
		}
		close(conn);
	}

	private void close(Connection conn, Statement st, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("close ResultSet failed:" + e.getMessage(), e);
			}
		}
		close(conn, st);
	}

	private void close(Statement st, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("close ResultSet failed:" + e.getMessage(), e);
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				log.error("close Statement failed:" + e.getMessage(), e);
			}
		}
	}

	private void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
				;
			} catch (SQLException e) {
				log.error("rollback failed:" + e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("serial")
	private final static List<Class<?>> PrimitiveClasses = new ArrayList<Class<?>>() {
		{
			add(Integer.class);
			add(Long.class);
			add(Float.class);
			add(Double.class);
			add(String.class);
			add(java.util.Date.class);
			add(java.sql.Date.class);
			add(java.sql.Timestamp.class);
		}
	};

	private final static boolean _IsPrimitive(Class<?> cls) {
		return cls.isPrimitive() || PrimitiveClasses.contains(cls);
	}

	private void showSql(String sql, Object... params) {
		log.info("SQL : " + sql + " ; params : " + Arrays.asList(params));
	}
}
