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
}
