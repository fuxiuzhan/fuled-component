package com.fxz.fuled.dynamic.kafka.pojo;

import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ConfigurationProperties(KafkaConsumerProperties.PREFIX)
@Configuration(proxyBeanMethods = false)
public class KafkaConsumerProperties {
    public static final String PREFIX = "fuled.dynamic.kafka.consumer";
    public static final String STATUS_START = "Start";
    public static final String STATUS_STOP = "Stop";
    private String defaultBeanName;
    private String defaultMethod;
    /**
     * <topic,config>
     */
    private Map<String, SingleConfig> configs;

    @Data
    public static class SingleConfig {
        /**
         * Start Stop
         */
        private String status = STATUS_START;
        private String beanName;
        private String methodName;
        private String id;
        private String containerFactory;
        private String containerGroup;
        private String groupId;
        private Integer concurrency = 1;
        private Boolean isBatch = Boolean.TRUE;
        private KafkaProperties properties = new KafkaProperties();
    }
}
