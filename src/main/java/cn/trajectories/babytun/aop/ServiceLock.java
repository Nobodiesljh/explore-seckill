package cn.trajectories.babytun.aop;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceLock {
    String description() default "";
}
