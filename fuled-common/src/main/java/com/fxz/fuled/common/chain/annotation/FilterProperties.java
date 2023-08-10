package com.fxz.fuled.common.chain.annotation;

import java.lang.annotation.*;

/**
 * FilterProperty
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FilterProperties {
    /**
     * 多个 FilterProperty
     *
     * @return
     */
    FilterProperty[] properties() default {};
}
