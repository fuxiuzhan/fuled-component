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
     * 拒绝任务数
     */
    private long rejectCount;

    /**
     * worker创建个数 如果突增，可能是线程异常过多导致，需关注
     */
    private long workerCreateCount;
    /**
     * 执行任务数
     */
    private long execCount;

    /**
     * 任务数
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
     * 曾经最大
     */
    private int largestPoolSize;

    /**
     * ttl in second
     */
    private long keepAliveInSeconds;

    /**
     *
     */
    private String version;

    /**
     *
     */
    private String jdkVersion;

}
