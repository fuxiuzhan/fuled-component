package com.fxz.fuled.simple.cache;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 参数注解
 * 当注解使用在接口上时，无法直接获取参数的名称
 * 需要使用@CacheParam注解指定名称
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface CacheParam {
    String value() default "";
}
