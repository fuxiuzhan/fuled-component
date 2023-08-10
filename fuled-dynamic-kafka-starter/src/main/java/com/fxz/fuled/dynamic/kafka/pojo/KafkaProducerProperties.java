package com.fxz.fuled.dynamic.kafka.pojo;

import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ConfigurationProperties(KafkaProducerProperties.PREFIX)
@Configuration(proxyBeanMethods = false)
public class KafkaProducerProperties {

    public static final String PREFIX = "fuled.dynamic.kafka.producer";

    /**
     * <producerName,config>
     */
    private Map<String, KafkaProducerProperties.SingleConfig> configs;

    @Data
    public static class SingleConfig {
        /**
         * properties
         */
        private KafkaProperties properties = new KafkaProperties();
    }
}
