package com.fxz.fuled.common.chain;

/**
 * 过滤器
 *
 * @param <T>
 * @author fxz
 */
public interface Filter<T> {
    /**
     * filter context
     * @param t
     * @param invoker
     * @param <R>
     * @return
     */
    <R> R filter(T t, Invoker invoker);
}
