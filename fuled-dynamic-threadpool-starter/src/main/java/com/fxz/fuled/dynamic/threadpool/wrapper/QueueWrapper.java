package com.fxz.fuled.dynamic.threadpool.wrapper;

import com.fxz.fuled.dynamic.threadpool.RpcContext;
import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * 包装queue
 */
public class QueueWrapper {
    public static BlockingQueue wrapper(BlockingQueue blockingQueue, ThreadExecuteHook threadExecuteHook) {
        return (BlockingQueue) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{BlockingQueue.class, Raw.class},
                (proxy, method, args) -> {
                    if ("offer".equals(method.getName())) {
                        //包装runnable
                        Object[] newArgs = args;
                        if (args[0] instanceof Runnable) {
                            newArgs = new Object[]{new RunnableWrapper((Runnable) args[0], RpcContext.get(), threadExecuteHook)};
                        } else if (args[0] instanceof Callable) {
                            //callable其实线程池也是包装成runnable进行运行的，所以这条逻辑不会执行到，
                            //如果是拦截入口的话就需要了
                            //原理提示作用
                            newArgs = new Object[]{new CallableWrapper<>((Callable) args[0], RpcContext.get())};
                        }
                        return blockingQueue.offer(newArgs[0]);
                    }
                    //获取被代理对象
                    if ("getNative".equals(method.getName())) {
                        return blockingQueue;
                    }
                    Method targetMethod = blockingQueue.getClass().getMethod(method.getName(), method.getParameterTypes());
                    try {
                        return targetMethod.invoke(blockingQueue, args);
                        //处理原始异常，不然使用代理的捕获异常机制会失效
                    } catch (InvocationTargetException ex) {
                        throw ex.getTargetException();
                    }
                });
    }

    public interface Raw {
        Object getNative();
    }
}
