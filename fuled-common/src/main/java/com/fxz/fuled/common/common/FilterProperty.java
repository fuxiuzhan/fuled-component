package com.fxz.fuled.common.common;

import java.lang.annotation.*;

/**
 * <p>
 * 管理Filter，包括类型，加载方式，加载顺序等
 * @author fxz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FilterProperty {
    String name() default "";

    int order() default 0;

    FilterType type() default FilterType.PRE;

    boolean enabled() default true;
}

