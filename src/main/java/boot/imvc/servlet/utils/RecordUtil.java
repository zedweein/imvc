package boot.imvc.servlet.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;

import boot.imvc.servlet.data.bean.Record;

public class RecordUtil {

	/**
	 * 将Bean转为Record对象 常用于批量插入时候
	 * 
	 * @param <T>
	 * 
	 * @param clazz
	 * @param rec
	 */
	public static Record record(Object obj) {
		// 1.先将bean转为map
		Record record = new Record();
		Map<String, Object> _map = new HashMap<String, Object>();
		try {
			Class<?> clazz = obj.getClass();
			for (Field field : clazz.getDeclaredFields()) {
				boolean access = field.isAccessible();
				field.setAccessible(Boolean.TRUE);
				String fieldName = field.getName();
				Object value = field.get(obj);
				// 对于框架生成的bean会有持久化Column name作为key
				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					String col = column.name();
					if (StringUtils.isNotEmpty(col)) {
						fieldName = col;
					}
					if (!column.nullable() && isNumbericType(field.getType())) {
						value = 0;
					}
				}
				// 排除final属性
				if (!Modifier.isFinal(field.getModifiers())) {
					_map.put(fieldName, value);
				}
				// 是否有非空限制
				field.setAccessible(access);
			}
			record.setColumns(_map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return record;
	}

	/**
	 * 批量将对象转为Record对象
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Record> recordList(Object obj) {
		List<Record> records = new ArrayList<Record>();
		@SuppressWarnings("unchecked")
		List<Object> objs = (List<Object>) obj;
		if (objs != null && objs.size() > 0) {
			for (Object _o : objs) {
				records.add(record(_o));
			}
		}
		return records;
	}

	private static boolean isNumbericType(Class<?> type) {
		return type == int.class || type == long.class || type == short.class || type == double.class || type == float.class || type == Integer.class || type == Long.class || type == Short.class || type == Double.class || type == Float.class || type == BigInteger.class || type == BigDecimal.class || type == Number.class;
	}
}
