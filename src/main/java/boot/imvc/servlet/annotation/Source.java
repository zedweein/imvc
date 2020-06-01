package boot.imvc.servlet.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 数据源标识
 * 默认first数据源
 * @author Awn
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface Source {
	String value() default "8b04d5e3775d298e78455efc5ca404d5";
}
