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

2. web.xml配置一个核心控制器<br>
boot.imvc.servlet.core.DispatcherServlet<br>

3. app.properties<br>
是否自动封装数据 ：  init-database=true<br>
核心配置注解扫描 ：package.scan=com.test.web<br>

4.jdbc.properties
如果配置 init-database=true
则需要在classpath下新建jdbc.properties，系统默认使用HikariCP
支持多个数据源，命名规则是 first,second,third...
其中first是系统默认数据源，不可修改前缀<br>
datasource.names=first,second<br>
#first<br>
first.datasource=com.zaxxer.hikari.HikariDataSource<br>
#first.driverClassName=oracle.jdbc.driver.OracleDriver<br>
first.driverClassName=com.mysql.cj.jdbc.Driver<br>
#first.jdbcUrl=jdbc:oracle:thin:@10.221.244.132:12647/sqmmt<br>
first.jdbcUrl=jdbc:mysql://xxx/tank?useSSL=false&serverTimezone=UTC<br>
first.username=test<br>
first.password=123456<br>
first.autoCommit=false<br>
first.connectionTimeout=60000<br>
first.idleTimeout=50000<br>
first.maxLifetime=60000<br>
first.minimumIdle=10<br>
first.maximumPoolSize=50<br>
first.validationTimeout=3000<br>
#second<br>
second.datasource=com.zaxxer.hikari.HikariDataSource<br>
#second.driverClassName=oracle.jdbc.driver.OracleDriver<br>
second.driverClassName=com.mysql.cj.jdbc.Driver<br>
#second.jdbcUrl=jdbc:oracle:thin:@10.221.244.132:12647/sqmmt<br>
second.jdbcUrl=jdbc:mysql://xxx/tank?useSSL=false&serverTimezone=UTC<br>
second.username=test<br>
second.password=123456<br>
second.autoCommit=false<br>
second.connectionTimeout=60000<br>
second.idleTimeout=50000<br>
second.maxLifetime=60000<br>
second.minimumIdle=10<br>
second.maximumPoolSize=50<br>
second.validationTimeout=3000<br>
mapper.scan=/mapper/<br>

5.mapper扫描<br>
jdbc.properties配置mapper文件扫描路径,mapper是一个标准的xml文件,参数使用@param 来占位，支持自带引号 '@param'<br>
支持root上配置数据源first，也支持单个sql配置数据源，优先使用sql上的source数据源<br>
mapper.scan=/mapper/<br>
xml示例：demo.xml<br>
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

6.关于注解<br>
@Action : MVC中 Controller层标识<br>
@Import : 依赖注入某个bean,支持自定义beanName,如 @Import, @Import("demoService")<br>
@Mapping ：解析HTTP请求uri,如@Mapping("/demo/test")<br>
@Param : 解析HTTP参数，如 @Param("name")<br>
@Prop : 注入系统配置参数，即web.xml中配置的configLocation的properties文件内配置参数，可以使用该注解来获取value，如 @Prop("init-database")<br>
@Service ： 服务层标识符<br>
@Source ： 数据源，可以使用@Source("first"),@Source("second"),来获取对应的数据源<br>

7.一切就绪？开始撸码：

------------------------------------------

@Action<br>
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

@Service<br>
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

8. 结果展示
http://127.0.0.1:8080/web_test/demo/test<br>
浏览器输出：<br>

[{"uuid":"4c3c1202-8da7-4191-5f9d-55ed6bbb61b5","sort":1585819610726,"update_time":"2020-04-03 01:43:38","create_time":"2020-04-03 01:26:51","role":"ADMINISTRATOR","username":"admin","password":"$2a$10$VhLJS3LuTQODoRd0SuRseeqyLK0X2Poh/KILTmGDsegK8sAyhln02","avatar_url":"","last_ip":"60.166.113.25","last_time":"2020-04-03 01:43:38","size_limit":-1,"total_size_limit":-1,"total_size":1156599,"status":"OK"}]





