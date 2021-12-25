package com.fxz.fuled.common.common;

import org.springframework.core.annotation.AliasFor;

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
public @interface FilterProperty {
    String name() default "";

    int order() default 0;

    @AliasFor("name")
    String filterName() default "";

    boolean enabled() default true;
}

