package com.fxz.fuled.threadpool.monitor.manage;

/**
 * @author fxz
 *
 * 线程执行hook
 */
public interface ThreadExecuteHook {

    /**
     * 线程执行前
     *
     * @param runnable
     */
    default void beforeExecute(Runnable runnable) {
    }

    /**
     * 线程执行后,无论正常还是异常都会调用
     *
     * @param runnable
     */
    default void afterExecute(Runnable runnable) {
    }

    /**
     * 线程执行异常
     *
     * @param runnable
     * @param throwable
     */
    default void onException(Runnable runnable, Throwable throwable) {
    }
}
