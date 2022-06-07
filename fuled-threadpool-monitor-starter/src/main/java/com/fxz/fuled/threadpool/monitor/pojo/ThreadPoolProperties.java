package com.fxz.fuled.threadpool.monitor.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "fuled.thread.pool")
@Configuration
public class ThreadPoolProperties {

    private Map<String, SimpleProp> config;

    @Data
    public static class SimpleProp {
        private int coreSize;
    }
}

