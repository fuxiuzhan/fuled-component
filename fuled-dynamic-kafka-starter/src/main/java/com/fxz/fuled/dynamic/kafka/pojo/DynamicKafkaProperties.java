package com.fxz.fuled.dynamic.kafka.pojo;


import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fxz.fuled.dynamic.kafka.pojo.DynamicKafkaProperties.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
@Configuration
@Data
public class DynamicKafkaProperties {
    public static final String PREFIX = "fuled.dynamic.kafka";

    private Map<String, SingleConfig> config = new HashMap<>();

    private Map<String, String> globalConfig = new HashMap<>();

    @Data
    public static class SingleConfig {
        private boolean enabled = Boolean.TRUE;
        private List<String> bootstrapServers;
        private String name;
        private String groupId;
        public String[] topics;
        private int concurrency = 1;
        /**
         * containerProps
         */
        private ContainerProperties containerProps = new ContainerProperties("");
        /**
         * props
         */
        private KafkaProperties.Consumer props = new KafkaProperties.Consumer();
        /**
         * listener bean
         */
        private String listenerBeanName;

    }
}
