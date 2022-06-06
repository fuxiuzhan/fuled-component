package com.fxz.fuled.threadpool.monitor.wrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;

/**
 * 包装queue
 */
public class QueueWrapper {
    public static BlockingQueue wrapper(BlockingQueue blockingQueue) {
        return (BlockingQueue) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{BlockingQueue.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("workQueue".equals(method.getName())) {
                            //包装runnable
                            return invoke(blockingQueue, method, args);
                        }
                        return invoke(blockingQueue, method, args);
                    }
                });
    }
}
