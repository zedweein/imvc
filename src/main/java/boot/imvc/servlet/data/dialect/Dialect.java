/**
 * Copyright (c) 2011-2017
 *
 */

package boot.imvc.servlet.data.dialect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import boot.imvc.servlet.data.bean.Record;

/**
 * Dialect.
 */
public abstract class Dialect {

	// Methods for common
	public String forTableBuilderDoBuild(String tableName){
		return "select * from " + tableName + " where 1 = 2";
	}

	public abstract String forPage(int start, int length, String sql);

	/*public abstract void forBeanSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras);

  	public abstract void forBeanUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql, List<Object> paras);
*/
	public void forDbSave(String tableName, Record record, StringBuilder sql, List<Object> paras){
		tableName = tableName.trim();

		sql.append("insert into ");
		sql.append(tableName).append("(");
		StringBuilder temp = new StringBuilder();
		temp.append(") values(");
		
		for (Entry<String, Object> e : record.getColumns().entrySet()) {
			if (paras.size() > 0) {
				sql.append(", ");
				temp.append(", ");
			}
			sql.append("\""+e.getKey().toUpperCase()+"\"");
			temp.append("?");
			paras.add(e.getValue());
		}

		sql.append(temp.toString()).append(")");
	}

	public void forDbUpdate(String tableName, Map<String, Object> keyMap,  Record record, StringBuilder sql, List<Object> paras){
		tableName = tableName.trim();

		sql.append("update ").append(tableName).append(" set ");
		for (Entry<String, Object> e : record.getColumns().entrySet()) {
			String colName = e.getKey().trim();
			if (!keyMap.keySet().contains(colName)) {
				if (paras.size() > 0) {
					sql.append(", ");
				}
				sql.append(colName).append(" = ? ");
				paras.add(e.getValue());
			}
		}
		if (null != keyMap && keyMap.size() > 0) {
			sql.append(" where ");
			int i = 0;
			for (Entry<String, Object> entry : keyMap.entrySet()) {
				if (i > 0) {
					sql.append(" and ");
				}
				sql.append(entry.getKey()).append(" = ?");
				paras.add(entry.getValue());
				i++;
			}
		}
	}

	public boolean isOracle() {
		return false;
	}
	
}






