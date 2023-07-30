package com.fxz.fuled.dynamic.kafka.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class KafkaContainerRegistry {
    @Getter
    @Setter
    kafkaClientRefresher kafkaClientRefresher;
    @Getter
    public static Map<String, MessageListenerContainer> containerMap = new ConcurrentHashMap<>();

    public static void registry(String topic, MessageListenerContainer listenerContainer) {
        if (containerMap.containsKey(topic)) {
            log.warn("topic->{} has bean registered,please rechecked skip...", topic);
        } else {
            containerMap.put(topic, listenerContainer);
        }
    }
}
