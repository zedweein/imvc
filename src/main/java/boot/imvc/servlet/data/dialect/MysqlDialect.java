/**
 * Copyright (c) 2011-2017
 */

package boot.imvc.servlet.data.dialect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import boot.imvc.servlet.data.bean.Record;

/**
 * MysqlDialect.
 */
public class MysqlDialect extends Dialect {
//
//	public String forTableBuilderDoBuild(String tableName) {
//		return "select * from `" + tableName + "` where 1 = 2";
//	}
//
	@Override
	public void forDbSave(String tableName, Record record, StringBuilder sql, List<Object> paras) {
		tableName = tableName.trim();

		sql.append("insert into `");
		sql.append(tableName).append("`(");
		StringBuilder temp = new StringBuilder();
		temp.append(") values(");

		for (Entry<String, Object> e : record.getColumns().entrySet()) {
			if (paras.size() > 0) {
				sql.append(", ");
				temp.append(", ");
			}
			sql.append("`").append(e.getKey()).append("`");
			temp.append("?");
			paras.add(e.getValue());
		}
		sql.append(temp.toString()).append(")");
	}

	@Override
	public void forDbUpdate(String tableName, Map<String, Object> keyMap, Record record, StringBuilder sql,
			List<Object> paras) {
		tableName = tableName.trim();

		sql.append("update `").append(tableName).append("` set ");
		for (Entry<String, Object> e : record.getColumns().entrySet()) {
			String colName = e.getKey().trim();
			if (!keyMap.keySet().contains(colName)) {
				if (paras.size() > 0) {
					sql.append(", ");
				}
				sql.append("`").append(colName).append("` = ? ");
				paras.add(e.getValue());
			}
		}
		if(null!= keyMap && keyMap.size()>0){
			sql.append(" where ");
			int i = 0;
			for(Entry<String, Object> entry : keyMap.entrySet()){
				if (i > 0) {
					sql.append(" and ");
				}
				sql.append("`").append(entry.getKey()).append("` = ?");
				paras.add(entry.getValue());
				i++;
			}
		}
	}

	/**
	 * limit can use one or two '?' to pass paras
	 */
	@Override
	public String forPage(int offset, int length, String sql) {
		StringBuilder ret = new StringBuilder(sql);
		ret.append(" limit ").append(offset).append(", ").append(length);
		return ret.toString();
	}
	
}
