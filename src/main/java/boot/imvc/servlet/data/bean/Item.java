package boot.imvc.servlet.data.bean;

import java.util.Map;

import boot.imvc.servlet.utils.TypeUtil;

public class Item {

	String id;

	String source;

	String value;

	/**
	 * 将参数解析到sql中，参数是@key
	 * 
	 * @param map
	 * @return
	 */
	public String parse(Map<String, Object> map) {
		String sql = value;
		if (map != null && !map.isEmpty()) {
			for (String key : map.keySet()) {
				sql = sql.replaceAll("@" + key, TypeUtil.toString(map.get(key)));
			}
		}
		return sql;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}