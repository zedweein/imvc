package boot.imvc.servlet.data.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Page
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = -5395997221963176643L;

	// -- 公共变量 --//
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	// -- 分页参数 --//
	protected int start = 1;
	protected int length = 20;
	// -- 排序参数 --//
	protected String order = null;
	protected String dir = null;

	// -- 返回结果 --//
	private List<T> data; // list result of this page
	private int recordsFiltered;// page numbers
	private int recordsTotal; // total row
	private double dbTime;

	/**
	 * Constructor.
	 * 
	 * @param list
	 *            the list of paginate result
	 * @param pageNo
	 *            the page number
	 * @param pageSize
	 *            the page size
	 * @param totalPage
	 *            the total page of paginate
	 * @param totalRow
	 *            the total row of paginate
	 */
	public Page(List<T> list, int start, int length, int totalRow) {
		this.data = list;
		this.start = start;
		this.length = length;
		this.recordsTotal = totalRow;
		this.recordsFiltered = totalRow;
	}

	public Page(int start, int length, int totalRow, String order, String dir) {
		this.start = start;
		this.length = length;
		this.recordsTotal = totalRow;
		this.order = order;
		this.dir = dir;
	}

	public Page(int start, int length, int totalRow) {
		this.start = start;
		this.length = length;
		this.recordsTotal = totalRow;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String value) {
		// 检查order字符串的合法值
		if (!DESC.equalsIgnoreCase(value) && !ASC.equalsIgnoreCase(value)) {
			throw new IllegalArgumentException("排序方向" + order + "不是合法值");
		}
		this.dir = value;
	}

	/**
	 * 返回Page对象自身的setOrderBy函数,可用于连续设置。
	 */
	public Page<T> sort(final String theOrderBy) {
		setDir(theOrderBy);
		return this;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String value) {
		this.order = value;
	}

	/**
	 * 返回Page对象自身的setOrder函数,可用于连续设置。
	 */
	public Page<T> order(final String theOrder) {
		setOrder(theOrder);
		return this;
	}

	/**
	 * 是否已设置排序字段,无默认值.
	 */
	public boolean isOrderBy() {
		return (StringUtils.isNoneBlank(dir) && StringUtils.isNoneBlank(order));
	}

	/**
	 * 是否已设置排序字段,无默认值.
	 */
	public String orderBy() {
		return order + " " + dir;
	}

	public List<T> getData() {
		return data;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsTotal(int total) {
		recordsTotal = total;
		recordsFiltered = total;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public double getDbTime() {
		return dbTime;
	}

	public void setDbTime(double dbTime) {
		this.dbTime = dbTime;
	}
}
