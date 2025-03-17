package com.fxz.fuled.dynamic.threadpool.wrapper;

import com.fxz.fuled.dynamic.threadpool.RpcContext;
import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;
import lombok.Getter;

import java.util.concurrent.Callable;

/**
 * @param <V>
 * @author fxz
 */
public class CallableWrapper<V> implements Callable<V>, TaskWrapper {
    private Callable callable;
    private Object meta;

    private ThreadExecuteHook threadExecuteHook;

    private String threadPoolName;

    @Getter
    private long bornTs;
    @Getter
    private long executeTs;
    @Getter
    private long queuedDuration;
    @Getter
    private long executeDuration;

    @Getter
    private long aliveDuration;
    @Getter
    private long completeTs;

    public CallableWrapper(Callable callable, Object meta, ThreadExecuteHook threadExecuteHook, String threadPoolName) {
        this.callable = callable;
        this.meta = meta;
        this.threadPoolName = threadPoolName;
        this.threadExecuteHook = threadExecuteHook;
        this.bornTs = System.currentTimeMillis();
        threadExecuteHook.enqueue(this);
    }

    @Override
    public V call() throws Exception {
        try {
            executeTs = System.currentTimeMillis();
            queuedDuration = executeTs - bornTs;
            RpcContext.set(meta);
            threadExecuteHook.beforeExecute(this);
            return (V) callable.call();
        } catch (Throwable t) {
            threadExecuteHook.onException(this, t);
            throw t;
        } finally {
            completeTs = System.currentTimeMillis();
            executeDuration = completeTs - executeTs;
            aliveDuration = completeTs - bornTs;
            threadExecuteHook.afterExecute(this);
            RpcContext.remove();
        }
    }

    @Override
    public String getThreadPoolName() {
        return threadPoolName;
    }

    @Override
    public long queuedDuration() {
        return queuedDuration;
    }

    @Override
    public long executedDuration() {
        return executeDuration;
    }

    @Override
    public long aliveDuration() {
        return aliveDuration;
    }
}
