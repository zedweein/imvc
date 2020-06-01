package boot.imvc.servlet.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * MVC中 Controller层标识
 * @author Awn
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Action {
	String value() default "";
}
