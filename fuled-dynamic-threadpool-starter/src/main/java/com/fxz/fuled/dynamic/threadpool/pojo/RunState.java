package com.fxz.fuled.dynamic.threadpool.pojo;

import lombok.Data;

/**
 * 瞬时指标可以通过Prometheus统计
 */
@Data
public class RunState {

    private String threadPoolName;
    /**
     * 线程等待时间（瞬时）
     */
    private long waitTime;
    /**
     * 线程执行时间（瞬时）
     */
    private long runningTime;
    /**
     * 最大等待时间
     */
    private long maxWaitTime;
    /**
     * 最大执行时间
     */
    private long maxRunningTime;

    public RunState(String threadPoolName, long waitTime, long runningTime) {
        this.threadPoolName = threadPoolName;
        this.waitTime = waitTime;
        this.runningTime = runningTime;
    }
}
