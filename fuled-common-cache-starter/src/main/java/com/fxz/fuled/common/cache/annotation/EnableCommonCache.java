package com.fxz.fuled.common.cache.annotation;

import com.fxz.fuled.common.cache.config.AutoCommonCacheConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fxz
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AutoCommonCacheConfig.class)
public @interface EnableCommonCache {
}
