# imvc
一款轻量级MVC架构框架，让你用很少的配置来架构一个web系统
1. 基于注解
2. 基于反射
3. 基于ioc
4. 一个jar包，
5. 配置化实现
6. 自动数据源映射
7. 高度封装
更多特性............
#实例
1. imvc-1.0.0.jar加入到classpath

2. web.xml配置一个核心控制器
<web-app>
  <servlet>
		<servlet-name>imvc</servlet-name>
		<servlet-class>boot.imvc.servlet.core.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>configLocation</param-name>
			<param-value>app.properties</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>imvc</servlet-name>
		<url-pattern>/*</url-pattern>
	</servlet-mapping>
</web-app>

3. app.properties
是否自动封装数据 ：  init-database=true
核心配置注解扫描 ：package.scan=com.test.web

4.jdbc.properties
如果配置 init-database=true
则需要在classpath下新建jdbc.properties，系统默认使用HikariCP
支持多个数据源，命名规则是 first,second,third...
其中first是系统默认数据源，不可修改前缀
datasource.names=first,second
#first
first.datasource=com.zaxxer.hikari.HikariDataSource
#first.driverClassName=oracle.jdbc.driver.OracleDriver
first.driverClassName=com.mysql.cj.jdbc.Driver
#first.jdbcUrl=jdbc:oracle:thin:@10.221.244.132:12647/sqmmt
first.jdbcUrl=jdbc:mysql://xxx/tank?useSSL=false&serverTimezone=UTC
first.username=test
first.password=123456
first.autoCommit=false
first.connectionTimeout=60000
first.idleTimeout=50000
first.maxLifetime=60000
first.minimumIdle=10
first.maximumPoolSize=50
first.validationTimeout=3000
#second
second.datasource=com.zaxxer.hikari.HikariDataSource
#second.driverClassName=oracle.jdbc.driver.OracleDriver
second.driverClassName=com.mysql.cj.jdbc.Driver
#second.jdbcUrl=jdbc:oracle:thin:@10.221.244.132:12647/sqmmt
second.jdbcUrl=jdbc:mysql://xxx/tank?useSSL=false&serverTimezone=UTC
second.username=test
second.password=123456
second.autoCommit=false
second.connectionTimeout=60000
second.idleTimeout=50000
second.maxLifetime=60000
second.minimumIdle=10
second.maximumPoolSize=50
second.validationTimeout=3000
mapper.scan=/mapper/

5.mapper扫描
jdbc.properties配置mapper文件扫描路径,mapper是一个标准的xml文件,参数使用@param 来占位，支持自带引号 '@param'
支持root上配置数据源first，也支持单个sql配置数据源，优先使用sql上的source数据源
mapper.scan=/mapper/
xml示例：demo.xml
<?xml version="1.0" encoding="utf-8"?>
<root source="first">
	<sql id="tank30_user">
		<![CDATA[
			select * from tank30_user where username = '@name'
		]]>
	</sql>

	<sql id="help_relation" source="second">
		<![CDATA[
			select * from help_relation where help_topic_id = @help_topic_id
		]]>
	</sql>
</root>

6.关于注解
@Action : MVC中 Controller层标识
@Import : 依赖注入某个bean,支持自定义beanName,如 @Import, @Import("demoService")
@Mapping ：解析HTTP请求uri,如@Mapping("/demo/test")
@Param : 解析HTTP参数，如 @Param("name")
@Prop : 注入系统配置参数，即web.xml中配置的configLocation的properties文件内配置参数，可以使用该注解来获取value，如 @Prop("init-database")
@Service ： 服务层标识符
@Source ： 数据源，可以使用@Source("first"),@Source("second"),来获取对应的数据源

7.一切就绪？开始撸码：

------------------------------------------
@Action
public class ActionMain {
	
	@Import
	private ServiceMain serviceMain;
	
	@Mapping("/demo/test")
	public void query(RequestContext rc) {
		List<Record> list = serviceMain.get();
		System.out.println(list);
		rc.write(list.toString());
	}
}
------------------------------------------

@Service
public class ServiceMain {
	
  //Executor 是系统服装的数据操纵对象，无需关系细节，封装了各个操作
	@Source("first")
	private Executor first;
	
	@Prop("package.scan")
	private String pack;
	
	public List<Record> get() {
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("name", "admin");
		return first.recordList(first.sql("tank30_user",param));
	}
	
}

------------------------------------------







