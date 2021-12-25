package com.fxz.fuled.common.chain;

/**
 * 过滤器
 *
 * @param <T>
 * @author fxz
 */
public interface Filter<T> {
    /**
     * @param t
     * @param invoker
     */
    <R> R filter(T t, Invoker invoker);
}
