package boot.imvc.servlet.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boot.imvc.servlet.annotation.Source;
import boot.imvc.servlet.annotation.Service;
import boot.imvc.servlet.data.Executor;
import boot.imvc.servlet.data.bean.Record;
import boot.imvc.servlet.demo.bean.Tank30User;

@Service
public class DemoService {
	
	@Source("first")
	private Executor first;
	
	@Source("second")
	private Executor second;
	
	public String get(String name) {
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("name", "admin");
		
		List<Tank30User> query = first.beanList(Tank30User.class,first.sql("tank30_user",param));
		System.out.println(query);
		
//		Map<String, Object> readMap = first.map("select * from tank30_user limit 1", new Object[] {});
//		System.out.println(readMap);
//		List<Record> query2 = first.recordList("select * from tank30_user", new Object[] {});
//		System.out.println(query2);
//		int proc = first.proc("call tank_user_num(@a)", new Object[] {});
//		System.out.println(proc);
		
		param.put("help_topic_id", "488");
		List<Record> recordList = second.recordList(first.sql("help_relation",param));
		
		System.out.println(recordList);
		return name + ",男，33岁,安徽省合肥人";
	}

}
