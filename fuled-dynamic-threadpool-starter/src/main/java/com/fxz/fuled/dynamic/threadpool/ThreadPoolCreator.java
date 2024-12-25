package com.fxz.fuled.dynamic.threadpool;


import org.springframework.util.Assert;

import java.util.concurrent.*;

/**
 * 线程池创建工具类
 */
public class ThreadPoolCreator {


    /**
     * @param name
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name) {
        return createThreadPoolExecutor(name, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), new ArrayBlockingQueue<>(1024), new ThreadPoolExecutor.AbortPolicy());

    }

    /**
     * @param name
     * @param coreSize
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name, int coreSize) {
        return createThreadPoolExecutor(name, coreSize, coreSize, new ArrayBlockingQueue<>(1024), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * @param name
     * @param coreSize
     * @param workQueue
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name, int coreSize, BlockingQueue<Runnable> workQueue) {
        return createThreadPoolExecutor(name, coreSize, coreSize, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * @param name
     * @param coreSize
     * @param maxCoreSize
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name, int coreSize, int maxCoreSize) {
        return createThreadPoolExecutor(name, coreSize, maxCoreSize, new ArrayBlockingQueue<>(1024), new ThreadPoolExecutor.AbortPolicy());

    }


    /**
     * @param name
     * @param coreSize
     * @param handler
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name, int coreSize, RejectedExecutionHandler handler) {
        return createThreadPoolExecutor(name, coreSize, coreSize, new ArrayBlockingQueue<>(1024), handler);

    }

    /**
     * @param name
     * @param coreSize
     * @param maxCoreSize
     * @param handler
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name, int coreSize, int maxCoreSize, RejectedExecutionHandler handler) {
        return createThreadPoolExecutor(name, coreSize, maxCoreSize, new ArrayBlockingQueue<>(1024), handler);
    }

    /**
     * @param name
     * @param coreSize
     * @param maxCoreSize
     * @param workQueue
     * @param handler
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name, int coreSize, int maxCoreSize, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        return createThreadPoolExecutor(name, coreSize, maxCoreSize, 0, TimeUnit.MINUTES, workQueue, r -> new Thread(r), handler);
    }

    /**
     * @param name
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     * @return
     */
    public static ThreadPoolExecutor createThreadPoolExecutor(String name, int corePoolSize,
                                                              int maximumPoolSize,
                                                              long keepAliveTime,
                                                              TimeUnit unit,
                                                              BlockingQueue<Runnable> workQueue,
                                                              ThreadFactory threadFactory,
                                                              RejectedExecutionHandler handler) {
        Assert.hasText(name, "ThreadPoolExecutor Name Not Null");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        ThreadPoolRegistry.registerThreadPool(name, threadPoolExecutor);
        return threadPoolExecutor;

    }
}
