package com.fxz.fuled.dynamic.kafka.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListenerConfigurationSelector;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaListenerConfigurationSelector.class)
@Import(kafkaClientRefresher.class)
public class KafkaClientConfig {
}
