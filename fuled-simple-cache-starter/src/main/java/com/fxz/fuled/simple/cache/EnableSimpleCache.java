package com.fxz.fuled.simple.cache;

import com.fxz.fuled.simple.cache.config.AutoConfigSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fxz
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AutoConfigSelector.class)
public @interface EnableSimpleCache {
}
