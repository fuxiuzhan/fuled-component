package com.fxz.fuled.dynamic.threadpool.manage;

import com.fxz.fuled.dynamic.threadpool.wrapper.RunnableWrapper;

/**
 * @author fxz
 *
 * 线程执行hook
 */
public interface ThreadExecuteHook extends ThreadLocalTransmitSupport{


    /**
     * 入队
     * @param runnableWrapper
     */
    default void enqueue(RunnableWrapper runnableWrapper){

    }
    /**
     * 线程执行前
     *
     * @param runnableWrapper
     */
    default void beforeExecute(RunnableWrapper runnableWrapper) {
    }

    /**
     * 线程执行后,无论正常还是异常都会调用
     *
     * @param runnableWrapper
     */
    default void afterExecute(RunnableWrapper runnableWrapper) {
    }

    /**
     * 线程执行异常
     *
     * @param runnableWrapper
     * @param throwable
     */
    default void onException(RunnableWrapper runnableWrapper, Throwable throwable) {
    }
}
