package boot.imvc.servlet.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 获取application.properties中的属性
 * @author Awn
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Prop {
	String value() default "";
}
