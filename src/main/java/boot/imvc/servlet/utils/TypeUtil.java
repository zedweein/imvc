package boot.imvc.servlet.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TypeUtil {
	public static final String toString(Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	public static final String toString(Object value, String defValue) {
		if (value == null) {
			return defValue;
		}
		return value.toString();
	}

	public static final Byte toByte(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (checkString(strVal)) {
				return null;
			}
			return Byte.parseByte(strVal);
		}
		throw new RuntimeException("can not cast to byte, value : " + value);
	}

	public static final Character toChar(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof String) {
			String strVal = (String) value;

			if (checkString(strVal)) {
				return null;
			}

			if (strVal.length() != 1) {
				throw new RuntimeException("can not cast to char, value : " + value);
			}

			return strVal.charAt(0);
		}

		throw new RuntimeException("can not cast to char, value : " + value);
	}

	public static final Short toShort(Object value) {
		return toShort(value, null);
	}

	public static final Short toShort(Object value, Short defValue) {
		if (value == null) {
			return defValue;
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (checkString(strVal)) {
				return defValue;
			}
			return Short.parseShort(strVal);
		}

		throw new RuntimeException("can not cast to short, value : " + value);
	}

	public static final BigDecimal toBigDecimal(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}

		String strVal = value.toString();
		if (checkString(strVal)) {
			return null;
		}

		return new BigDecimal(strVal);
	}

	public static final BigInteger toBigInteger(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}

		if (value instanceof Float || value instanceof Double) {
			return BigInteger.valueOf(((Number) value).longValue());
		}

		String strVal = value.toString();
		if (checkString(strVal)) {
			return null;
		}

		return new BigInteger(strVal);
	}

	public static final Float toFloat(Object value) {
		return toFloat(value, null);
	}

	public static final Float toFloat(Object value, Float defValue) {
		if (value == null) {
			return defValue;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof String) {
			String strVal = value.toString();
			if (checkString(strVal)) {
				return defValue;
			}
			if (strVal.indexOf(',') != 0) {
				strVal = strVal.replaceAll(",", "");
			}
			return Float.parseFloat(strVal);
		}

		throw new RuntimeException("can not cast to float, value : " + value);
	}

	public static final Double toDouble(Object value) {
		return toDouble(value, null);
	}

	public static final Double toDouble(Object value, Double defValue) {
		if (value == null) {
			return defValue;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof String) {
			String strVal = value.toString();
			if (checkString(strVal)) {
				return defValue;
			}
			if (strVal.indexOf(',') != 0) {
				strVal = strVal.replaceAll(",", "");
			}
			return Double.parseDouble(strVal);
		}

		throw new RuntimeException("can not cast to double, value : " + value);
	}

	public static final Date toDate(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return (Date) value;
		}

		if (value instanceof Calendar) {
			return ((Calendar) value).getTime();
		}

		long longValue = -1;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
			return new Date(longValue);
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (checkString(strVal)) {
				return null;
			}

			if (strVal.indexOf('-') != -1) {
				String format;
				if (strVal.length() == 10) {
					format = "yyyy-MM-dd";
				} else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
					format = "yyyy-MM-dd HH:mm:ss";
				} else {
					format = "yyyy-MM-dd HH:mm:ss.SSS";
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				try {
					return dateFormat.parse(strVal);
				} catch (ParseException e) {
					throw new RuntimeException("can not cast to Date, value : " + strVal);
				}
			}

			longValue = Long.parseLong(strVal);
		}

		if (longValue <= 0) {
			throw new RuntimeException("can not cast to Date, value : " + value);
		}

		return new Date(longValue);
	}

	public static final java.sql.Date toSqlDate(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof java.sql.Date) {
			return (java.sql.Date) value;
		}

		if (value instanceof Date) {
			return new java.sql.Date(((Date) value).getTime());
		}

		if (value instanceof Calendar) {
			return new java.sql.Date(((Calendar) value).getTimeInMillis());
		}

		long longValue = 0;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (checkString(strVal)) {
				return null;
			}
			if (strVal.indexOf('-') != -1) {
				return new java.sql.Date(toDate(strVal).getTime());
			}
			longValue = Long.parseLong(strVal);
		}

		if (longValue <= 0) {
			throw new RuntimeException("can not cast to sql Date, value : " + value);
		}

		return new java.sql.Date(longValue);
	}

	public static final Timestamp toTimestamp(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Calendar) {
			return new Timestamp(((Calendar) value).getTimeInMillis());
		}

		if (value instanceof Timestamp) {
			return (Timestamp) value;
		}

		if (value instanceof Date) {
			return new Timestamp(((Date) value).getTime());
		}

		long longValue = 0;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (checkString(strVal)) {
				return null;
			}
			if (strVal.indexOf('-') != -1) {
				return new Timestamp(toDate(strVal).getTime());
			}
			longValue = Long.parseLong(strVal);
		}

		if (longValue <= 0) {
			throw new RuntimeException("can not cast to sql Timestamp, value : " + value);
		}

		return new Timestamp(longValue);
	}

	public static final Long toLong(Object value) {
		return toLong(value, null);
	}

	public static final Long toLong(Object value, Long defValue) {
		if (value == null) {
			return defValue;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (checkString(strVal)) {
				return defValue;
			}

			if (strVal.indexOf(',') != 0) {
				strVal = strVal.replaceAll(",", "");
			}

			try {
				return Long.parseLong(strVal);
			} catch (NumberFormatException ex) {
				//
			}
		}
		throw new RuntimeException("can not cast to long, value : " + value);
	}

	public static final Integer toInt(Object value) {
		return toInt(value, null);
	}

	public static final Integer toInt(Object value, Integer defValue) {
		if (value == null) {
			return defValue;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;

			if (checkString(strVal)) {
				return defValue;
			}

			if (strVal.indexOf(',') != 0) {
				strVal = strVal.replaceAll(",", "");
			}

			return Integer.parseInt(strVal);
		}

		throw new RuntimeException("can not cast to int, value : " + value);
	}

	public static final Boolean toBoolean(Object value) {
		if (value == null) {
			return Boolean.FALSE;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof String) {
			String str = (String) value;

			if (checkString(str)) {
				return false;
			}

			if ("true".equalsIgnoreCase(str) || "t".equalsIgnoreCase(str) || "1".equals(str)) {
				return Boolean.TRUE;
			}
			if ("false".equalsIgnoreCase(str) || "f".equalsIgnoreCase(str) || "0".equals(str)) {
				return Boolean.FALSE;
			}
		}

		throw new RuntimeException("can not cast to boolean, value : " + value);
	}

	public static final byte[] toBytes(Object value) {
		if (value instanceof byte[]) {
			return (byte[]) value;
		}

		if (value instanceof String) {
			return Base64.decode((String) value, Base64.DEFAULT);
		}
		throw new RuntimeException("can not cast to int, value : " + value);
	}

	/**
	 * test for all types of mysql
	 *
	 * 表单提交测试结果: 1: 表单中的域,就算不输入任何内容,也会传过来 "", 也即永远不可能为 null. 2: 如果输入空格也会提交上来 3:
	 * 需要考 model中的 string属性,在传过来 "" 时是该转成null还是不该转换, 我想, 因为用户没有输入那么肯定是 null,
	 * 而不该是 ""
	 *
	 * 注意: 1:当clazz参数不为String.class, 且参数s为空串blank的情况, 此情况下转换结果为 null, 而不应该抛出异常
	 * 2:调用者需要对被转换数据做 null 判断，参见 ModelInjector 的两处调用
	 */
	@SuppressWarnings({ "unchecked" })
	public static final <T> T cast(Class<T> clazz, Object obj) {
		if (obj == null) {
			return null;
		}

		if (clazz == null) {
			throw new IllegalArgumentException("clazz is null");
		}

		if (clazz == obj.getClass()) {
			return (T) obj;
		}

		if (clazz.isAssignableFrom(obj.getClass())) {
			return (T) obj;
		}

		if (clazz == String.class) {
			// mysql type: varchar, char, enum, set, text, tinytext, mediumtext,
			// longtext
			return ("".equals(obj) ? null : (T) toString(obj)); // 用户在表单域中没有输入内容时将提交过来
																// "",
																// 因为没有输入,所以要转成
																// null.
		}

		if (clazz == boolean.class || clazz == Boolean.class) {
			return (T) toBoolean(obj);
		}

		if (clazz == byte.class || clazz == Byte.class) {
			return (T) toByte(obj);
		}

		if (clazz == short.class || clazz == Short.class) {
			return (T) toShort(obj);
		}

		if (clazz == int.class || clazz == Integer.class) {
			return (T) toInt(obj);
		}

		if (clazz == long.class || clazz == Long.class) {
			return (T) toLong(obj);
		}

		if (clazz == float.class || clazz == Float.class) {
			return (T) toFloat(obj);
		}

		if (clazz == double.class || clazz == Double.class) {
			return (T) toDouble(obj);
		}

		if (clazz == BigDecimal.class) {
			return (T) toBigDecimal(obj);
		}

		if (clazz == BigInteger.class) {
			return (T) toBigInteger(obj);
		}

		if (clazz == Date.class) {
			return (T) toDate(obj);
		}

		if (clazz == java.sql.Date.class) {
			return (T) toSqlDate(obj);
		}

		if (clazz == Timestamp.class) {
			return (T) toTimestamp(obj);
		}
		throw new RuntimeException("can not cast to : " + clazz.getName());
	}

	/**
	 * 检查字符串是否为空或者等于NULL字符串
	 */
	private static boolean checkString(String str) {
		return str.length() == 0 //
				|| "null".equals(str) //
				|| "NULL".equals(str);
	}
}
