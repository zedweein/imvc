/**
 * Copyright (c) 2011-2017
 */

package boot.imvc.servlet.data.dialect;

/**
 * SqlServerDialect 为OSC
 * @author admin
 * 网友战五渣贡献代码：http://www.oschina.net/question/2333909_234198
 */
public class SqlServerDialect extends Dialect {

	/**
	 * sql.replaceFirst("(?i)select", "") 正则中带有 "(?i)" 前缀，指定在匹配时不区分大小写
	 */
	@Override
	public String forPage(int begin, int length, String sql) {
		int end = begin + length;
		StringBuilder ret = new StringBuilder();
		ret.append("select * from ( select row_number() over (order by tempcolumn) temprownumber, * from ");
		ret.append(" ( select top ").append(end).append(" tempcolumn=0,");
		ret.append(sql.replaceFirst("(?i)select", ""));
		ret.append(")vip)mvp where temprownumber>").append(begin);
		return ret.toString();
	}
}
