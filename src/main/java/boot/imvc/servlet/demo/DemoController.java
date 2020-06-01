package boot.imvc.servlet.demo;

import boot.imvc.servlet.annotation.Action;
import boot.imvc.servlet.annotation.Mapping;
import boot.imvc.servlet.annotation.Prop;
import boot.imvc.servlet.annotation.Import;
import boot.imvc.servlet.core.RequestContext;
import boot.imvc.servlet.utils.MD5Util;

@Action
@Mapping("/demo")
public class DemoController {

	@Import
	private DemoService service;
	
	@Prop("test.key")
	private String key;
	
	@Mapping("/query")
	public void query(RequestContext rc) {
		String name = rc.param("name");
		String value = rc.prop("test.key");
		String result = service.get(name);
		rc.write(result+", rc prop = " + value+", prop annotation = " + key);
	}
	
	public static void main(String[] args) {
		System.out.println(MD5Util.lower32("first"));
	}
	
}
