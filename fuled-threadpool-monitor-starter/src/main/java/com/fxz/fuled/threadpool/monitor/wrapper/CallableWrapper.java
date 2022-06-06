package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;

import java.util.concurrent.Callable;

/**
 * @param <V>
 * @author fxz
 */
public class CallableWrapper<V> implements Callable<V> {
    private Callable callable;
    private Object meta;

    public CallableWrapper(Callable callable, Object meta) {
        this.callable = callable;
        this.meta = meta;
    }

    @Override
    public V call() throws Exception {
        try {
            RpcContext.set(meta);
            return (V) callable.call();
        } finally {
            RpcContext.remove();
        }
    }
}
