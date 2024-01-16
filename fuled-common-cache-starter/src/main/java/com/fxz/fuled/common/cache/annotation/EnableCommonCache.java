package com.fxz.fuled.common.cache.annotation;

import com.fxz.fuled.common.cache.config.AutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fxz
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AutoConfig.class)
public @interface EnableCommonCache {
}
