package com.fxz.fuled.dynamic.kafka.config;

import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListenerConfigurationSelector;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaListenerConfigurationSelector.class)
@Import(kafkaClientRefresher.class)
public class KafkaClientConfig {


    @Bean("dynamicKafkaVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-dynamic-kafka.version", "1.0.0.waterdrop", "fuled-dynamic-kafka-component");
    }
}
