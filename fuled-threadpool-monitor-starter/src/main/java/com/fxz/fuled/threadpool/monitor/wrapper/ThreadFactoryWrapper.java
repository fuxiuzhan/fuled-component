package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;

import java.util.concurrent.ThreadFactory;

/**
 * 通过包装threadFactory实现threadLocal
 * 上下文的传递
 * 双管齐下
 * 一是包装threadFactory
 * 二是包装queue
 * 当线程池初始化时或者核心线程数小于设定值
 * 时会使用threadFactory创建线程，此过程是同步过程
 * 所以可以通过重写threadFactory实现threadLocal传递
 * <p>
 * 当核心线程满时，任务会被放到queue中
 * 此时需要对放入队列的任务进行包装即可，
 * 放入队列的动作是同步执行，由父线程执行
 *
 * @author fxz
 */
public class ThreadFactoryWrapper implements ThreadFactory {

    private ThreadFactory threadFactory;

    public ThreadFactoryWrapper(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public Thread newThread(Runnable r) {
        return threadFactory.newThread(new RunnableWrapper(r, RpcContext.get()));
    }
}
