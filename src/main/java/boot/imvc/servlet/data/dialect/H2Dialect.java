package boot.imvc.servlet.data.dialect;

public class H2Dialect extends Dialect {

	@Override
	public String forPage(int offset, int length, String sql) {
		StringBuilder ret = new StringBuilder(sql);
		ret.append(" limit ").append(offset).append(", ").append(length);
		return ret.toString();
	}
}
