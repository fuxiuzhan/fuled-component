package com.fxz.fuled.common.common;

import java.lang.annotation.*;

/**
 * <p>
 * 管理Filter，包括类型，加载方式，加载顺序等
 *
 * @author fxz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FilterProperty {
    /**
     * name example: DNS_PRE_BLACK_FILTER
     *
     * @return
     */
    String name() default "";

    int order() default 0;

    /**
     * group example :PRE,POST..
     *
     * @return
     */
    String filterGroup() default "";

    boolean enabled() default true;
}

