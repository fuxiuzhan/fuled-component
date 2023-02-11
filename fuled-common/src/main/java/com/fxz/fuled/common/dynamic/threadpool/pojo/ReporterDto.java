package com.fxz.fuled.common.dynamic.threadpool.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author fxz
 */
@Data
public class ReporterDto {
    /**
     * 时间戳
     */
    private long timeStamp = System.currentTimeMillis();
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * endpoint ip
     */
    private List<String> ips;
    /**
     * 线程池类型
     */
    private String threadPoolType;
    /**
     * 核心线程数
     */
    private int corePoolSize;
    /**
     * 最大线程数
     */
    private int maximumPoolSize;
    /**
     * 当前线程数
     */
    private int currentPoolSize;
    /**
     * 拒绝线程数
     */
    private long rejectCnt;
    /**
     * 执行线程数
     */
    private long execCount;

    /**
     * 执行任务数
     */
    private long taskCount;

    /**
     * 活跃线程数
     */
    private long activeCount;
    /**
     * 队列类型
     */
    private String queueType;
    /**
     * 队列最大长度
     */
    private int queueMaxSize;
    /**
     * 当前队列长度
     */
    private int currentQueueSize;
    /**
     * 拒绝策略
     */
    private String rejectHandlerType;
    /**
     * 最近线程执行时间
     */
    private long recentExecTimeStamp;

    /**
     * 队列最大等待
     */
    private int largestPoolSize;

    /**
     * 时间统计是大概统计
     * <p>
     * 因为采样率的问题不可能精确
     * <p>
     * 如果想要精确可以在具体的方法
     * 上使用@Timed 注解
     * <p>
     * 线程等待时间（瞬时）
     */
    private long waitTime;
    /**
     * 线程执行时间（瞬时）
     * (排除worker)
     */
    private long runningTime;
    /**
     * 最大等待时间
     */
    private long maxWaitTime;
    /**
     * 最大执行时间
     * (排除worker)
     */
    private long maxRunningTime;
}
