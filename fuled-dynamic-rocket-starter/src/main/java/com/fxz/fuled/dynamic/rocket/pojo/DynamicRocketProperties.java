package com.fxz.fuled.dynamic.rocket.pojo;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.fxz.fuled.dynamic.rocket.pojo.DynamicRocketProperties.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
@Configuration
@Data
public class DynamicRocketProperties {
    public static final String PREFIX = "fuled.dynamic.rocket";

    private Map<String, SingleConfig> config = new HashMap<>();


    @Data
    public static class SingleConfig {
        private String name;
        private String nameServer;
        private boolean enabled = Boolean.TRUE;
        private ConsumerProperties consumer = new ConsumerProperties();
        private String listenerBeanName;
    }
}
