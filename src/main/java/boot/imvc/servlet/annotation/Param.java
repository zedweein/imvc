package boot.imvc.servlet.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *  参数标识
 * @author Awn
 *
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Param {
	String value() default "";
}
