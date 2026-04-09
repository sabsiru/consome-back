package consome.infrastructure.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** Rate limit key prefix (e.g. "login", "register") */
    String key();

    /** Maximum requests allowed within the time window */
    int limit();

    /** Time window duration */
    int window() default 1;

    /** Time window unit */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /** IP-based (true) or User-based (false) */
    boolean byIp() default false;
}
