package com.example.fuled.dynamic.redis.annotation;

import com.example.fuled.dynamic.redis.config.DynamicConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fxz
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicConfig.class)
public @interface EnableDynamicRedis {
}