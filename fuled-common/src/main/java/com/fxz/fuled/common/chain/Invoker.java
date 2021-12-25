package com.fxz.fuled.common.chain;

/**
 * 责任链invoker，组装过滤链使用
 *
 * @param <T>
 */
public interface Invoker<T> {
    /**
     * 调用下一个invoker
     *
     * @param t
     */
    <R> R invoke(T t);
}
