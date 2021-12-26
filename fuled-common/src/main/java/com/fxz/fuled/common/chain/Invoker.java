package com.fxz.fuled.common.chain;

/**
 * 责任链invoker，组装过滤链使用
 *
 * @author fxz
 * @param <T>
 */
public interface Invoker<T> {
    /**
     * invoke next
     * @param t
     * @param <R>
     * @return
     */
    <R> R invoke(T t);
}
