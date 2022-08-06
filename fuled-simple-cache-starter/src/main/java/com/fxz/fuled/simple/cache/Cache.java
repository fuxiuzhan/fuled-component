package com.fxz.fuled.simple.cache;


import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xiuzhan.fu
 * <p>
 * 轻量级的切面缓存工具，支持任意超时时间及
 * EL表达式 ，防止springcache的缓存雪崩问题
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Cache {
    /**
     * 缓存主键 支持EL表达式，默认采用hashcode（方法+入参列表。。。）
     *
     * @return
     */
    String key() default "";

    @AliasFor("key")
    String value() default "";

    /**
     * 前缀
     *
     * @return
     */
    String prefix() default "";

    /**
     * 缓存过期时间，启用本地缓存情况下两个缓存的超时时间一致
     *
     * @return
     */
    int expr() default 10;

    /**
     * 过期时间单位
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.MINUTES;

    /**
     * 缓存操作
     *
     * @return
     */
    CacheOpTypeEnum opType() default CacheOpTypeEnum.SAVE;

    /**
     * 是否启用本地缓存，本地缓存默认采用LRU+Expr淘汰策略
     *
     * @return
     */
    boolean localTurbo() default false;

    /**
     * 是否填充返回null值
     *
     * @return
     */
    boolean includeNullResult() default false;

    /**
     * 条件表达式
     *
     * @return
     */
    String condition() default "";

    /**
     * 只使用本地缓存
     *
     * @return
     */
    boolean localCacheOnly() default false;

    /**
     * 清除本地缓存
     *
     * @return
     */
    boolean clearLocal() default false;
}

