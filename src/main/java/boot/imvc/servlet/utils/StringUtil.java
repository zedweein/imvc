package boot.imvc.servlet.utils;

public class StringUtil {
	
	/**
	 * 首字母小写
	 * @param string
	 */
	public static String firstLower(String string) {
		char[] charArray = string.toCharArray();
		charArray[0] += 32;
		return String.valueOf(charArray);
	}
	
}
