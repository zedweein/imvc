/**
 * Copyright (c) 2011-2017
 */

package boot.imvc.servlet.data.dialect;

import java.util.List;
import java.util.Map.Entry;

import boot.imvc.servlet.data.bean.Record;

/**
 * OracleDialect.
 * @author admin
 */
public class OracleDialect extends Dialect {
	@Override
	public String forTableBuilderDoBuild(String tableName) {
		return "select * from " + tableName + " where rownum < 1";
	}

	@Override
	public void forDbSave(String tableName, Record record, StringBuilder sql, List<Object> paras) {
		tableName = tableName.trim();

		sql.append("insert into ");
		sql.append(tableName).append("(");
		StringBuilder temp = new StringBuilder();
		temp.append(") values(");

		int count = 0;
		for (Entry<String, Object> e : record.getColumns().entrySet()) {
			String colName = e.getKey();
			if (count++ > 0) {
				sql.append(", ");
				temp.append(", ");
			}
			sql.append("\""+colName.toUpperCase()+"\"");

			Object value = e.getValue();
			if (value instanceof String && ((String) value).endsWith(".nextval")) {
				temp.append(value);
			} else {
				temp.append("?");
				paras.add(value);
			}
		}
		sql.append(temp.toString()).append(")");
	}

	@Override
	public String forPage(int start, int length, String sql) {
		int end = start + length;
		StringBuilder ret = new StringBuilder();
		ret.append("select * from ( select row_.*, rownum rownum_ from (  ");
		ret.append(sql);
		ret.append(" ) row_ where rownum <= ").append(end).append(") table_alias");
		ret.append(" where table_alias.rownum_ > ").append(start);
		return ret.toString();
	}

	@Override
	public boolean isOracle() {
		return true;
	}
}
