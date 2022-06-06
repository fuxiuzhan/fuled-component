package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @param <V>
 * @author fxz
 */
public class CallableWrapper<V> implements Callable<V> {
    private Callable callable;
    private Map<Object, Object> metaMap;

    public CallableWrapper(Callable callable, Map<Object, Object> metaMap) {
        this.callable = callable;
        this.metaMap = metaMap;
    }

    @Override
    public V call() throws Exception {
        try {
            RpcContext.set(metaMap);
            return (V) callable.call();
        } finally {
            RpcContext.remove();
        }
    }
}
