package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * 包装queue
 */
public class QueueWrapper {
    public static BlockingQueue wrapper(BlockingQueue blockingQueue) {
        return (BlockingQueue) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{BlockingQueue.class, Raw.class},
                (proxy, method, args) -> {
                    if ("offer".equals(method.getName())) {
                        //包装runnable
                        Object[] newArgs = args;
                        if (args[0] instanceof Runnable) {
                            newArgs = new Object[]{new RunnableWrapper((Runnable) args[0], RpcContext.get())};
                        } else if (args[0] instanceof Callable) {
                            newArgs = new Object[]{new CallableWrapper<>((Callable) args[0], RpcContext.get())};
                        }
                        return blockingQueue.offer(newArgs[0]);
                    }
                    //获取被代理对象
                    if ("getNative".equals(method.getName())) {
                        return blockingQueue;
                    }
                    Method targetMethod = blockingQueue.getClass().getMethod(method.getName(), method.getParameterTypes());
                    return targetMethod.invoke(blockingQueue, args);
                });
    }

    public interface Raw {
        Object getNative();
    }
}
