/**
 * 
 */
package boot.imvc.servlet.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 服务层标识
 * @author Awn
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Service {
	String value() default "";
}
