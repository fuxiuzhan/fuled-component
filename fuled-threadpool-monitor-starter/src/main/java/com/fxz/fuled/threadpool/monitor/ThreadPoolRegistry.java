package com.fxz.fuled.threadpool.monitor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fxz
 */
public class ThreadPoolRegistry {
    /**
     * 先处理ThreadPoolExecutor 以后处理TaskExecutor
     */
    private Map threadPoolExecutorHashMap = new ConcurrentHashMap<String, ThreadPoolExecutor>();
}
