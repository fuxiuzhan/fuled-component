package com.fxz.fuled.common.dynamic.threadpool.reporter;

/**
 * 桥接结构
 * 用于传递隔离的dynamicThreadPool和Repoter
 */
public interface FastStatReporter {

    /**
     * 更新瞬时指标
     * <p>
     * Repoter倾向于慢速或者静态指标
     * <p>
     * 收集的结果
     * <p>
     * 此处更新执行时的瞬时结果
     *
     * @param threadPoolName
     * @param queuedDuration
     * @param executeDuration
     */
    default void updateStat(String threadPoolName, long queuedDuration, long executeDuration, long aliveDuration) {
    }

}
