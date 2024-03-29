package com.fxz.fuled.dynamic.kafka.config;

import com.fxz.fuled.dynamic.kafka.pojo.KafkaConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.MultiMethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class kafkaClientRefresher implements SmartInitializingSingleton, ApplicationContextAware {
    private ApplicationContext applicationContext;

    /**
     * @param changedKeys
     */
    public void refresh(List<String> changedKeys) {
        KafkaConsumerProperties kafkaConsumerProperties = applicationContext.getBean(KafkaConsumerProperties.class);
        Binder.get(applicationContext.getEnvironment()).bind(KafkaConsumerProperties.PREFIX, Bindable.ofInstance(kafkaConsumerProperties));
        if (CollectionUtils.isEmpty(changedKeys)) {
            kafkaConsumerProperties.getConfigs().forEach((k, v) -> {
                //init
                buildContainer(k, v, kafkaConsumerProperties);
            });
        } else {
            for (String topic : changedKeys) {
                if (kafkaConsumerProperties.getConfigs().containsKey(topic)) {
                    //add or changed
                    if (KafkaContainerRegistry.containerMap.containsKey(topic)) {
                        //changed
                        if (KafkaConsumerProperties.STATUS_START.equalsIgnoreCase(kafkaConsumerProperties.getConfigs().get(topic).getStatus())) {
                            stopContainer(topic);
                            startContainer(topic);
                            log.info("restart Container->{}", topic);
                        } else {
                            stopContainer(topic);
                            log.info("stop Container->{}", topic);
                        }
                    } else {
                        buildContainer(topic, kafkaConsumerProperties.getConfigs().get(topic), kafkaConsumerProperties);
                    }
                } else {
                    //remove
                    if (KafkaContainerRegistry.getContainerMap().containsKey(topic)) {
                        stopContainer(topic);
                    }
                }
            }
        }
    }

    /**
     * @param topic
     */
    private void startContainer(String topic) {
        MessageListenerContainer messageListenerContainer = KafkaContainerRegistry.getContainerMap().get(topic);
        if (Objects.nonNull(messageListenerContainer)) {
            if (!messageListenerContainer.isRunning()) {
                messageListenerContainer.start();
                log.info("messageListenerContainer started topic->{}", topic);
            }
        }
    }

    /**
     * @param topic
     */
    private void stopContainer(String topic) {
        MessageListenerContainer messageListenerContainer = KafkaContainerRegistry.getContainerMap().get(topic);
        if (Objects.nonNull(messageListenerContainer)) {
            if (messageListenerContainer.isRunning()) {
                messageListenerContainer.stop();
                log.info("messageListenerContainer stopped topic->{}", topic);
            }
        }
    }

    /**
     * @param topic
     * @param singleConfig
     * @param kafkaConsumerProperties
     */
    private void buildContainer(String topic, KafkaConsumerProperties.SingleConfig singleConfig, KafkaConsumerProperties kafkaConsumerProperties) {
        String beanName = StringUtils.isEmpty(singleConfig.getBeanName()) ? kafkaConsumerProperties.getDefaultBeanName() : singleConfig.getBeanName();
        String methodName = StringUtils.isEmpty(singleConfig.getMethodName()) ? kafkaConsumerProperties.getDefaultMethod() : singleConfig.getMethodName();
        Object bean = applicationContext.getBean(beanName);
        Method method = Arrays.stream(bean.getClass().getDeclaredMethods()).filter(m -> m.getName().equalsIgnoreCase(methodName)).findFirst().get();
        MultiMethodKafkaListenerEndpoint endpoint =
                new MultiMethodKafkaListenerEndpoint<>(Arrays.asList(method), method, bean);
        endpoint.setBean(bean);
        endpoint.setId("endpoint-" + (StringUtils.isEmpty(singleConfig.getId()) ? topic : singleConfig.getId()));
        endpoint.setGroupId(singleConfig.getGroupId());
        endpoint.setTopics(topic);
        endpoint.setConcurrency(singleConfig.getConcurrency());
        endpoint.setBatchListener(singleConfig.getIsBatch());
        Properties properties = new Properties();
        properties.putAll(singleConfig.getProperties().buildConsumerProperties());
        endpoint.setConsumerProperties(properties);
        DefaultMessageHandlerMethodFactory defaultMessageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        endpoint.setMessageHandlerMethodFactory(defaultMessageHandlerMethodFactory);
        KafkaListenerContainerFactory containerFactory = applicationContext.getBean(KafkaListenerContainerFactory.class);
//        endpoint.setAutoStartup(singleConfig.getAutoStartup());
        /**
         * BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ConcurrentMessageListenerContainer.class);
         * beanDefinitionBuilder.addConstructorArgValue(consumerFactory);
         * beanDefinitionBuilder.addConstructorArgValue(properties);
         * beanDefinitionBuilder.addPropertyValue("concurrency", kafkaProperties.getConcurrency());
         * String beanName = String.format("%skafkaMessageListenerContainer", kafkaProperties.getName());
         * beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
         * ConcurrentMessageListenerContainer messageListenerContainer = (ConcurrentMessageListenerContainer) this.beanFactory.getBean(beanName);
         * messageListenerContainer.start();
         * currentSourceNames.add(kafkaProperties.getName());
         */
        MessageListenerContainer listenerContainer = containerFactory.createListenerContainer(endpoint);
        if (KafkaConsumerProperties.STATUS_START.equalsIgnoreCase(singleConfig.getStatus())) {
            if (!listenerContainer.isRunning()) {
                listenerContainer.start();
                log.info("listenerContainer started->{}", topic);
            }
        }
        KafkaContainerRegistry.registry(topic, listenerContainer);
        log.info("messageListener registered topic->{}", topic);
    }

    @Override
    public void afterSingletonsInstantiated() {
        refresh(Arrays.asList());
    }

    @EventListener
    public void eventListener(EnvironmentChangeEvent environmentChangeEvent) {
        Object source = environmentChangeEvent.getSource();
        if (source instanceof HashSet) {
            HashSet<String> kvs = (HashSet) source;
            List<String> topics = kvs.stream()
                    .filter(s -> s.startsWith(KafkaConsumerProperties.PREFIX))
                    .map(s -> s.replace(KafkaConsumerProperties.PREFIX + ".", "")
                            .split(",")[0])
                    .distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(topics)) {
                log.info("topics changed->{}", topics);
                refresh(topics);
                log.info("listenerContainer refreshed");
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

