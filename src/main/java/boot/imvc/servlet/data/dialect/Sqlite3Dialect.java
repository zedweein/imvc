/**
 * Copyright (c) 2011-2017
 */

package boot.imvc.servlet.data.dialect;

/**
 * SqliteDialect.
 * @author admin
 */
public class Sqlite3Dialect extends Dialect {
	@Override
	public String forPage(int offset, int length, String sql) {
		StringBuilder ret = new StringBuilder(sql);
		ret.append(" limit ").append(offset).append(", ").append(length);
		return ret.toString();
	}
}
