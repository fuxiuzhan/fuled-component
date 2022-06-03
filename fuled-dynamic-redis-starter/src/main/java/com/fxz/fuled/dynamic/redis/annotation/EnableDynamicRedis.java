package com.fxz.fuled.dynamic.redis.annotation;

import com.fxz.fuled.dynamic.redis.config.DynamicConfig;
import com.fxz.fuled.dynamic.redis.properties.DynamicProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fxz
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DynamicConfig.class, DynamicProperties.class})
public @interface EnableDynamicRedis {
}
