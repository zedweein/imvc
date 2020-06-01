package boot.imvc.servlet.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import boot.imvc.servlet.annotation.Action;
import boot.imvc.servlet.annotation.Import;
import boot.imvc.servlet.annotation.Mapping;
import boot.imvc.servlet.annotation.Prop;
import boot.imvc.servlet.annotation.Service;
import boot.imvc.servlet.annotation.Source;
import boot.imvc.servlet.data.DataSource;
import boot.imvc.servlet.data.Executor;
import boot.imvc.servlet.utils.MD5Util;
import boot.imvc.servlet.utils.PackageUtil;
import boot.imvc.servlet.utils.StringUtil;

public class DispatcherHelper {

	private Logger log = Logger.getLogger(DispatcherHelper.class);

	// 初始化配置文件
	private static final String CONFIG = "configLocation";

	// 配置信息
	private Properties prop = new Properties();

	// 保存所有扫描到的类
	private Set<Class<?>> scanClasses = new HashSet<Class<?>>();

	// IOC容器保存所有实例化对象
	private Map<String, Object> ioc = new HashMap<String, Object>();

	// 保存所有HandlerMapping与方法之间的关系
	private Map<String, Method> mapping = new HashMap<String, Method>();

	// 初始化
	public void init(ServletConfig config) throws Exception {

		initProperties(config);

		String scanPackage = "boot.imvc.servlet.data";
		initSystemScanner(scanPackage);

		String location = config.getInitParameter(CONFIG);
		scanPackage = (String) prop.get("package.scan");
		if (StringUtils.isEmpty(location) || StringUtils.isEmpty(scanPackage)) {
			throw new Exception("No 'package.scan' Found In " + location);
		}

		initClassScanner(scanPackage);

		initClassInstance();

		initDataSource(location);

		initInjections();

		initHandlerMapping();

		initedFramework();
	}

	private void initSystemScanner(String scanPackage) throws Exception {
		Set<Class<?>> classes = PackageUtil.classes(scanPackage);
		//本地服务器版本
		/*
			scanPackage = scanPackage.replaceAll("\\.", "/");
			URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
			File pack = new File(url.getFile());
			if (!pack.exists()) {
				throw new Exception(scanPackage + " Not Exist");
			}
			for (File file : pack.listFiles()) {
				if (file.isDirectory()) {
					initSystemScanner(scanPackage + "." + file.getName());
				} else {
					classNames.add(scanPackage + "." + file.getName().replace(".class", "").trim());
				}
			}
		*/
		//jar包版本
		scanClasses.addAll(classes);
	}

	

	private void initDataSource(String location) throws Exception {
		String init = (String) prop.get("init-database");
		Properties properties = new Properties();
		if (init != null && Boolean.parseBoolean(init)) {
			InputStream stream = this.getClass().getResourceAsStream("/jdbc.properties");
			if (stream == null) {
				throw new Exception("'init-database' Option in '" + location + "' is true, But No 'jdbc.properties' Found In Class Path");
			}
			properties.load(stream);
			Map<String, Executor> dsIoc = DataSource.initDataSource(properties);
			this.ioc.putAll(dsIoc);
		}
	}

	private void initProperties(ServletConfig config) throws Exception {
		String location = config.getInitParameter(CONFIG);
		if (StringUtils.isEmpty(location)) {
			throw new Exception("No init-param '" + CONFIG + "' Found In web.xml");
		}
		InputStream stream = null;
		try {
			stream = this.getClass().getResourceAsStream("/"+location);
			prop.load(stream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	// 初始化类扫描器
	private void initClassScanner(String scanPackage) throws Exception {
		URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
		File pack = new File(url.getFile());
		if (!pack.exists()) {
			throw new Exception(scanPackage + " Not Exist");
		}
		for (File file : pack.listFiles()) {
			if (file.isDirectory()) {
				initClassScanner(scanPackage + "." + file.getName());
			} else {
				log.info(scanPackage + "." + file.getName().replace(".class", "").trim());
				scanClasses.add(Class.forName(scanPackage + "." + file.getName().replace(".class", "").trim()));
			}
		}
	}

	// 初始化扫描类实例
	private void initClassInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (scanClasses.size() > 0) {
			for (Class<?> clazz : scanClasses) {
				// 当前class是Action注解
				if (clazz.isAnnotationPresent(Action.class)) {
					String beanName = StringUtil.firstLower(clazz.getSimpleName());
					ioc.put(beanName, clazz.newInstance());
				}
				// 当前class是Service注解
				else if (clazz.isAnnotationPresent(Service.class)) {
					// 1.优先使用自定义beanName，@Service("demoService")
					Service service = clazz.getAnnotation(Service.class);
					String beanName = service.value();
					if (StringUtils.isNotEmpty(beanName)) {
						ioc.put(beanName, clazz.newInstance());
					} else {
						// 父类的引用指向子类对象-创建 DemoService service = new
						// DemoServiceImpl()
						// 多个接口类型，则创建一个多个接口类型对象引用
						Class<?>[] interfaces = clazz.getInterfaces();
						if (interfaces.length > 0) {
							for (Class<?> iface : interfaces) {
								ioc.put(iface.getName(), clazz.newInstance());
							}
						} else {
							beanName = StringUtil.firstLower(clazz.getSimpleName());
							ioc.put(beanName, clazz.newInstance());
						}
					}
				} else {
					continue;
				}

			}
			log.info(ioc.keySet().toString());
		}
	}

	// 初始化依赖注入
	private void initInjections() throws IllegalArgumentException, IllegalAccessException {

		if (!ioc.isEmpty()) {
			Set<String> beanNames = ioc.keySet();
			for (String beanName : beanNames) {
				Object instance = ioc.get(beanName);
				// 反射获取所有的bean的属性
				Field[] fields = instance.getClass().getDeclaredFields();
				// 将这些属性赋值
				if (fields.length > 0) {
					for (Field field : fields) {
						// 判断是否被@Import修饰,是则依赖注入
						if (field.isAnnotationPresent(Import.class)) {
							Import source = field.getAnnotation(Import.class);
							// 获取自定义bean名称
							String sourceName = source.value().trim();
							if (StringUtils.isEmpty(sourceName)) {
								sourceName = StringUtil.firstLower(field.getType().getSimpleName());
							}
							// 如果是private私有的属性，则设置可访问
							field.setAccessible(Boolean.TRUE);
							field.set(instance, ioc.get(sourceName));
						}

						else if (field.isAnnotationPresent(Source.class)) {
							Source data = field.getAnnotation(Source.class);
							// 获取自定义bean名称
							String dataName = data.value().trim();
							// 如果是private私有的属性，则设置可访问
							field.setAccessible(Boolean.TRUE);
							field.set(instance, ioc.get(MD5Util.lower32(dataName)));
						}

						// 获取配置文件注解
						else if (field.isAnnotationPresent(Prop.class)) {
							Prop source = field.getAnnotation(Prop.class);
							String key = source.value().trim();
							// 如果是private私有的属性，则设置可访问
							field.setAccessible(Boolean.TRUE);
							field.set(instance, prop.get(key));
						}
					}
				}
			}
		}

	}

	// 初始化映射
	private void initHandlerMapping() {
		if (!ioc.isEmpty()) {
			Set<String> beanNames = ioc.keySet();
			for (String beanName : beanNames) {
				Class<?> clazz = ioc.get(beanName).getClass();
				// mapping信息保存在Action中
				if (clazz.isAnnotationPresent(Action.class)) {
					String requestUrl = "";
					// 修饰控制器的Mapping是基础路径
					if (clazz.isAnnotationPresent(Mapping.class)) {
						Mapping mapping = clazz.getAnnotation(Mapping.class);
						requestUrl = mapping.value();
					}

					// 获取所有方法
					Method[] methods = clazz.getMethods();
					if (methods.length > 0) {
						for (Method method : methods) {
							// 修饰方法的Mapping
							if (method.isAnnotationPresent(Mapping.class)) {
								Mapping mapped = method.getAnnotation(Mapping.class);
								requestUrl += mapped.value();
								mapping.put(requestUrl, method);
							}
						}
					}

				}
			}
		}
	}

	// 完成框架服务后打印
	private void initedFramework() {
		log.info("-------------------------");
		log.info("IMVC Framework inited ...");
		log.info("-------------------------");
	}

	// 请求分发
	public void disPatcher(RequestContext rc) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (!mapping.isEmpty()) {
			String requestUrl = rc.uri();
			String contextPath = rc.path();
			requestUrl = requestUrl.replace(contextPath, "").replaceAll("/+", "/");
			if (!mapping.containsKey(requestUrl)) {
				rc.write("<font style='font-size:25px;color:#777777'>404 Not Found</font><hr/>" + requestUrl);
				return;
			}
			// 将参数赋值给@Param注解的属性
			Map<String, Object> params = rc.params();

			Method method = mapping.get(requestUrl);
			Class<?>[] paramTypes = method.getParameterTypes();
			Object[] paramValues = new Object[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) {
				Class<?> paramTypeClass = paramTypes[i];
				// 将request赋值
				if (paramTypeClass == HttpServletRequest.class) {
					paramValues[i] = rc.request();
				}
				// 将response赋值
				else if (paramTypeClass == HttpServletResponse.class) {
					paramValues[i] = rc.response();
				} else if (paramTypeClass == RequestContext.class) {
					paramValues[i] = rc;
				}
				// 将String类型赋值
				else if (paramTypeClass == String.class) {
					for (Entry<String, Object> param : params.entrySet()) {
						paramValues[i] = (String) param.getValue();
					}
				}
			}
			// 调用当前这个bean的method，并将参数传入
			String beanName = StringUtil.firstLower(method.getDeclaringClass().getSimpleName());
			method.invoke(ioc.get(beanName), paramValues);
		}
	}

	public Properties prop() {
		return prop;
	}

	public void ioc(String key, Object instance) {
		this.ioc.put(key, instance);
	}

}
