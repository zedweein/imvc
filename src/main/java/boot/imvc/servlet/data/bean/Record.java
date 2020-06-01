package boot.imvc.servlet.data.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import boot.imvc.servlet.utils.GsonUtil;
import boot.imvc.servlet.utils.TypeUtil;

/**
 * Record
 * 
 * @author admin
 */
public class Record implements Serializable {
	private static final long serialVersionUID = 905784513600884082L;
	private Map<String, Object> columns = new LinkedHashMap<String, Object>();

	// Only used by RecordBuilder
	public void setColumnsMap(Map<String, Object> cols) {
		columns = cols;
	}

	/**
	 * Return columns map.
	 */
	public Map<String, Object> getColumns() {
		return columns;
	}

	/**
	 * Set columns value with map.
	 * 
	 * @param columns
	 *            the columns map
	 */
	public Record setColumns(Map<String, Object> cols) {
		columns.putAll(cols);
		return this;
	}

	/**
	 * Set columns value with Record.
	 * 
	 * @param record
	 *            the Record object
	 */
	public Record setColumns(Record record) {
		columns.putAll(record.getColumns());
		return this;
	}

	/**
	 * Returns true if this map contains a mapping for the specified key. More
	 * formally, returns true if and only if this map contains a mapping for a
	 * key k such that (key==null ? k==null : key.equals(k)). (There can be at
	 * most one such mapping.)
	 * 
	 * @param column
	 *            the column name of the record
	 */
	public boolean hasKey(Object column) {
		return columns.containsKey(column);
	}

	/**
	 * Remove attribute of this record.
	 * 
	 * @param column
	 *            the column name of the record
	 */
	public Record remove(String column) {
		columns.remove(column);
		return this;
	}

	/**
	 * Remove columns of this record.
	 * 
	 * @param columns
	 *            the column names of the record
	 */
	public Record remove(String... cols) {
		if (cols != null) {
			for (String c : cols) {
				columns.remove(c);
			}
		}
		return this;
	}

	/**
	 * Remove columns if it is null.
	 */
	public Record removeNullValueColumns() {
		for (Iterator<Entry<String, Object>> it = getColumns().entrySet().iterator(); it.hasNext();) {
			Entry<String, Object> e = it.next();
			if (e.getValue() == null) {
				it.remove();
			}
		}
		return this;
	}

	/**
	 * Keep columns of this record and remove other columns.
	 * 
	 * @param columns
	 *            the column names of the record
	 */
	public Record keep(String... cols) {
		if (cols != null && cols.length > 0) {
			Map<String, Object> newColumns = new LinkedHashMap<String, Object>(cols.length);
			for (String c : cols) {
				if (columns.containsKey(c)) {
					// prevent put null value to the newColumns
					newColumns.put(c, columns.get(c));
				}
			}
			columns.clear();
			columns.putAll(newColumns);
		} else {
			columns.clear();
		}
		return this;
	}

	/**
	 * Keep column of this record and remove other columns.
	 * 
	 * @param column
	 *            the column names of the record
	 */
	public Record keep(String column) {
		if (columns.containsKey(column)) { // prevent put null value to the
											// newColumns
			Object keepIt = columns.get(column);
			columns.clear();
			columns.put(column, keepIt);
		} else {
			columns.clear();
		}
		return this;
	}

	/**
	 * Remove all columns of this record.
	 */
	public Record clear() {
		columns.clear();
		return this;
	}

	/**
	 * Set column to record.
	 * 
	 * @param column
	 *            the column name
	 * @param value
	 *            the value of the column
	 */
	public Record set(String column, Object value) {
		columns.put(column, value);
		return this;
	}

	/**
	 * Get column of any mysql type
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String column) {
		return (T) columns.get(column);
	}

	/**
	 * Get column of any mysql type. Returns defaultValue if null.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String column, Object defaultValue) {
		Object result = columns.get(column);
		return (T) (result != null ? result : defaultValue);
	}

	/**
	 * Get column of mysql type: varchar, char, enum, set, text, tinytext,
	 * mediumtext, longtext
	 */
	public String getStr(String column) {
		Object value = columns.get(column);
		return TypeUtil.toString(value);
	}

	/**
	 * Get column of mysql type: varchar, char, enum, set, text, tinytext,
	 * mediumtext, longtext
	 */
	public String getStr(String column, String defValue) {
		Object value = columns.get(column);
		return TypeUtil.toString(value, defValue);
	}

	/**
	 * Get column of mysql type: int, integer, tinyint(n) n > 1, smallint,
	 * mediumint
	 */
	public Integer getInt(String column) {
		Object value = columns.get(column);
		return TypeUtil.toInt(value);
	}

	/**
	 * Get column of mysql type: int, integer, tinyint(n) n > 1, smallint,
	 * mediumint
	 */
	public Integer getInt(String column, Integer defValue) {
		Object value = columns.get(column);
		return TypeUtil.toInt(value, defValue);
	}

	/**
	 * Get column of mysql type: bigint
	 */
	public Long getLong(String column) {
		Object value = columns.get(column);
		return TypeUtil.toLong(value);
	}

	/**
	 * Get column of mysql type: bigint
	 */
	public Long getLong(String column, Long defValue) {
		Object value = columns.get(column);
		return TypeUtil.toLong(value, defValue);
	}

	/**
	 * Get column of mysql type: unsigned bigint
	 */
	public BigInteger getBigInteger(String column) {
		Object value = columns.get(column);
		return TypeUtil.toBigInteger(value);
	}

	/**
	 * Get column of mysql type: date, year
	 */
	public Date getDate(String column) {
		Object value = columns.get(column);
		return TypeUtil.toDate(value);
	}

	/**
	 * Get column of mysql type: time
	 */
	public java.sql.Time getTime(String column) {
		return (java.sql.Time) columns.get(column);
	}

	/**
	 * Get column of mysql type: timestamp, datetime
	 */
	public java.sql.Timestamp getTimestamp(String column) {
		Object value = columns.get(column);
		return TypeUtil.toTimestamp(value);
	}

	/**
	 * Get column of mysql type: real, double
	 */
	public Double getDouble(String column) {
		Object value = columns.get(column);
		return TypeUtil.toDouble(value);
	}

	/**
	 * Get column of mysql type: real, double
	 */
	public Double getDouble(String column, Double defValue) {
		Object value = columns.get(column);
		return TypeUtil.toDouble(value, defValue);
	}

	/**
	 * Get column of mysql type: float
	 */
	public Float getFloat(String column) {
		Object value = columns.get(column);
		return TypeUtil.toFloat(value);
	}

	/**
	 * Get column of mysql type: float
	 */
	public Float getFloat(String column, Float defValue) {
		Object value = columns.get(column);
		return TypeUtil.toFloat(value, defValue);
	}

	/**
	 * Get column of mysql type: bit, tinyint(1)
	 */
	public Boolean getBoolean(String column) {
		Object value = columns.get(column);
		return TypeUtil.toBoolean(value);
	}

	/**
	 * Get column of mysql type: decimal, numeric
	 */
	public BigDecimal getBigDecimal(String column) {
		Object value = columns.get(column);
		return TypeUtil.toBigDecimal(value);
	}

	/**
	 * Get column of mysql type: binary, varbinary, tinyblob, blob, mediumblob,
	 * longblob I have not finished the test.
	 */
	public byte[] getBytes(String column) {
		Object value = columns.get(column);
		if (value == null) {
			return null;
		}
		return TypeUtil.toBytes(value);
	}

	/**
	 * Get column of any type that extends from Number
	 */
	public Number getNumber(String column) {
		return (Number) columns.get(column);
	}

	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Record)) {
			return false;
		}
		if (o == this) {
			return true;
		}
		return columns.equals(((Record) o).getColumns());
	}

	@Override
	public int hashCode() {
		return columns == null ? 0 : columns.hashCode();
	}

	/**
	 * Return column names of this record.
	 */
	public String[] getColumnNames() {
		Set<String> attrNameSet = columns.keySet();
		return attrNameSet.toArray(new String[attrNameSet.size()]);
	}

	/**
	 * Return column values of this record.
	 */
	public Object[] getColumnValues() {
		Collection<Object> attrValueCollection = columns.values();
		return attrValueCollection.toArray(new Object[attrValueCollection.size()]);
	}

	/**
	 * Return json string of this record.
	 */
	public String toJson() {
		return GsonUtil.toJson(columns);
	}
}
