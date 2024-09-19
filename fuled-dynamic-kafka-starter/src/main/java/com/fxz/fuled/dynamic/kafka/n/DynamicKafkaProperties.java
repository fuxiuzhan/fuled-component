package com.fxz.fuled.dynamic.kafka.n;


import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.fxz.fuled.dynamic.kafka.n.DynamicKafkaProperties.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
@Configuration
@Data
public class DynamicKafkaProperties {
    public static final String PREFIX = "fuled.dynamic.kafka";

    private Map<String, SingleConfig> config = new HashMap<>();

    private Map<String, Object> globalConfig = new HashMap<>();

    @Data
    public static class SingleConfig {
        /**
         * properties
         */
        private String name;
        private String[] topics;
        private Integer concurrency = 1;
        private KafkaProperties properties = new KafkaProperties();
        private String kafkaListenerBeanName;

    }

}
