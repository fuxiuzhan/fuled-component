package com.fxz.fuled.common.cache.annotation;


import com.fxz.fuled.common.cache.enums.CacheOpTypeEnum;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 轻量级的切面缓存工具，支持任意超时时间及
 * EL表达式 ，防止springcache的缓存雪崩问题
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Cache {
    String key() default "";
    String prefix() default "";
    int expr() default 10;
    TimeUnit unit() default TimeUnit.MINUTES;
    CacheOpTypeEnum opType() default CacheOpTypeEnum.SAVE;
    boolean includeNullResult() default false;
    String condition() default "";

    boolean localTurbo() default false;
    boolean localCacheOnly() default false;
    boolean clearLocal() default false;

    //cacheAspect->cacheChain->LocalCache->ExtendCache->DefaultFianlCache
    //single&batch
}

