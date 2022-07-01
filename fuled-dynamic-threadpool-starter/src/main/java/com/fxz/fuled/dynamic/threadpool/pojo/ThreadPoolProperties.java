package com.fxz.fuled.dynamic.threadpool.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "fuled.thread.pool")
@Configuration(proxyBeanMethods = false)
public class ThreadPoolProperties {

    /**
     * 是否包装容器内的线程池
     */
    private boolean wrapper = Boolean.FALSE;
    /**
     * 配置线程池的参数
     */
    private Map<String, SimpleProp> config;

    @Data
    public static class SimpleProp {
        /**
         * 线程池核心线程数数量
         */
        private int coreSize;
    }
}

