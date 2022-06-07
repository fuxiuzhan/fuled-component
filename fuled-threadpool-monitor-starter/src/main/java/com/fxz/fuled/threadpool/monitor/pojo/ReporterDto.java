package com.fxz.fuled.threadpool.monitor.pojo;

import lombok.Data;

/**
 * @author fxz
 */
@Data
public class ReporterDto {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 线程池名称
     */
    private String threadPoolName;
    /**
     * 线程池类型
     */
    private String threadPoolType;
    /**
     * 核心线程数
     */
    private Integer corePoolSize;
    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;
    /**
     * 当前线程数
     */
    private Integer currentPoolSize;
    /**
     * 拒绝线程数
     */
    private Long rejectCnt;
    /**
     * 执行线程数
     */
    private Long execCount;
    /**
     * 队列类型
     */
    private String queueType;
    /**
     * 队列最大长度
     */
    private Integer queueMaxSize;
    /**
     * 当前队列长度
     */
    private Integer currentQueueSize;
    /**
     * 拒绝策略
     */
    private String rejectHandlerType;
    /**
     * 最近线程执行时间
     */
    private Long recentExecTimeStamp;
}
