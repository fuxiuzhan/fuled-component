package com.fxz.fuled.dynamic.threadpool.wrapper;

/**
 * 类型标识
 */
public interface TaskWrapper {
    /**
     * 获取对应的线程池名称
     *
     * @return
     */
    default String getThreadPoolName() {
        return "";
    }

    /**
     * 队列等待时间
     *
     * @return
     */
    default long queuedDuration() {
        return 0;
    }

    /**
     * 执行时间
     *
     * @return
     */
    default long executedDuration() {
        return 0;
    }

    /**
     * 存活时间=队列时间+执行时间
     *
     * @return
     */
    default long aliveDuration() {
        return 0;
    }

    /**
     * 是否worker
     *
     * @return
     */
    default boolean isWorker() {
        return Boolean.FALSE;
    }
}
