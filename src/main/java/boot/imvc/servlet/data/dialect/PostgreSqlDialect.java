/**
 * Copyright (c) 2011-2017
 */

package boot.imvc.servlet.data.dialect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import boot.imvc.servlet.data.bean.Record;
import boot.imvc.servlet.utils.TypeUtil;

/**
 * PostgreSqlDialect.
 */
public class PostgreSqlDialect extends Dialect {

//	public String forTableBuilderDoBuild(String tableName) {
//		return "select * from \"" + tableName + "\" where 1 = 2";
//	}
//
	@Override
	public void forDbSave(String tableName, Record record, StringBuilder sql, List<Object> paras) {
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
			sql.append("\"").append(e.getKey()).append("\"");
			Object value = e.getValue();
			if(e.getKey().equalsIgnoreCase("geom") || (value instanceof String && TypeUtil.toString(value).toUpperCase().startsWith("ST_"))){
				temp.append(e.getValue());
			}else{
				temp.append("?");
				paras.add(e.getValue());
			}
		}
		sql.append(temp.toString()).append(")");
	}

	@Override
	public void forDbUpdate(String tableName, Map<String, Object> keyMap, Record record, StringBuilder sql,
			List<Object> paras) {
		tableName = tableName.trim();

		sql.append("update ").append(tableName).append(" set ");
		for (Entry<String, Object> e : record.getColumns().entrySet()) {
			String colName = e.getKey().trim();
			if (!keyMap.keySet().contains(colName)) {
				if (paras.size() > 0) {
					sql.append(", ");
				}
				Object value = e.getValue();
				if(e.getKey().equalsIgnoreCase("geom") || (value instanceof String && TypeUtil.toString(value).toUpperCase().startsWith("ST_"))){
					sql.append("\"").append(colName).append("\" = ").append(value);
				}else{
					sql.append("\"").append(colName).append("\" = ? ");
					paras.add(e.getValue());
				}
			}
		}
		if (null != keyMap && keyMap.size() > 0) {
			sql.append(" where ");
			int i = 0;
			for (Entry<String, Object> entry : keyMap.entrySet()) {
				if (i > 0) {
					sql.append(" and ");
				}
				sql.append("\"").append(entry.getKey()).append("\" = ?");
				paras.add(entry.getValue());
				i++;
			}
		}
	}

	@Override
	public String forPage(int offset, int length, String sql) {
		StringBuilder ret = new StringBuilder(sql);
		ret.append(" limit ").append(length).append(" offset ").append(offset);
		return ret.toString();
	}
	
}
