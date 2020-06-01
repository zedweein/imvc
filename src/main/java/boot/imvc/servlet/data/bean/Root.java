package boot.imvc.servlet.data.bean;

import java.util.List;

public class Root {
	
	private String source;
	
	private List<Item> items;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
}
