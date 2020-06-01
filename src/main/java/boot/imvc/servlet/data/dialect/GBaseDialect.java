/**
 * Copyright (c) 2011-2017
 */

package boot.imvc.servlet.data.dialect;

/**
 * GBaseDialect.
 */
public class GBaseDialect extends Dialect {
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
