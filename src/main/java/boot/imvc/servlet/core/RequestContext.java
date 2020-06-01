package boot.imvc.servlet.core;

import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestContext {

	private HttpServletRequest request;

	private HttpServletResponse response;

	private Properties prop;
	
	public RequestContext(HttpServletRequest req, HttpServletResponse resp, Properties prop) {
		this.request = req;
		this.response = resp;
		this.prop = prop;
	}

	/**
	 * 加载配置信息，可选
	 * 
	 * @param prop
	 */
	public void loadProp(Properties prop) {
		this.prop = prop;
	}

	/**
	 * 获取参数
	 * 
	 * @param name
	 * @return
	 */
	public String param(String name) {
		return request.getParameter(name);
	}

	/**
	 * 获取所有参数
	 * 
	 * @return
	 */
	public Map<String, Object> params() {
		Map<String, Object> params = new TreeMap<String, Object>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String value = request.getParameter(paramName);
			params.put(paramName, value);
		}
		return params;
	}

	/**
	 * 客户端输出
	 * 
	 * @param content
	 */
	public void write(String content) {
		try {
			Writer w = response.getWriter();
			w.write(content);
			w.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取session
	 * 
	 * @return
	 */
	public HttpSession session() {
		return request.getSession();
	}

	/**
	 * 获取Session中的属性
	 * 
	 * @param name
	 * @return
	 */
	public Object sessionAttr(String name) {
		return this.session().getAttribute(name);
	}

	/**
	 * 获取URI
	 * 
	 * @return
	 */
	public String uri() {
		return request.getRequestURI();
	}

	/**
	 * 获取URL
	 * 
	 * @return
	 */
	public String url() {
		return request.getRequestURL().toString();
	}

	/**
	 * 获取ContextPath 如 /demo
	 * 
	 * @return
	 */
	public String path() {
		return request.getContextPath();
	}

	/**
	 * 获取web.xml中configLocation中的配置
	 * 
	 * @param key
	 * @return
	 */
	public String prop(String key) {
		return (String) prop.get(key);
	}

	public HttpServletRequest request() {
		return request;
	}

	public HttpServletResponse response() {
		return response;
	}

}
