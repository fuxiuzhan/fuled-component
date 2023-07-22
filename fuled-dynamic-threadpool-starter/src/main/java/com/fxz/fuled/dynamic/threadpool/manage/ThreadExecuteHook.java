package com.fxz.fuled.dynamic.threadpool.manage;

import com.fxz.fuled.dynamic.threadpool.wrapper.TaskWrapper;

/**
 * @author fxz
 * <p>
 * 线程执行hook
 */
public interface ThreadExecuteHook extends ThreadLocalTransmitSupport {


    /**
     * 入队
     *
     * @param taskWrapper
     */
    default void enqueue(TaskWrapper taskWrapper) {

    }

    /**
     * 线程执行前
     *
     * @param taskWrapper
     */
    default void beforeExecute(TaskWrapper taskWrapper) {
    }

    /**
     * 线程执行后,无论正常还是异常都会调用
     *
     * @param taskWrapper
     */
    default void afterExecute(TaskWrapper taskWrapper) {
    }

    /**
     * 线程执行异常
     *
     * @param taskWrapper
     * @param throwable
     */
    default void onException(TaskWrapper taskWrapper, Throwable throwable) {
    }
}
