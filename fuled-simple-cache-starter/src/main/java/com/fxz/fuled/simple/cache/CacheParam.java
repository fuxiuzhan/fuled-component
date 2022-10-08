package com.fxz.fuled.simple.cache;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 参数注解
 * 当注解使用在接口上时，无法直接获取参数的名称
 * 需要使用@CacheParam注解指定名称
 * 在jdk1.8环境下 增加-parameters编译参数
 * 也是可以保留接口参数名的，不过为了稳定下
 * 还是建议增加参数注解来标明参数
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface CacheParam {
    String value() default "";
}
