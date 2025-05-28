package com.fxz.fuled.dynamic.kafka.manager;

import com.fxz.fuled.dynamic.kafka.pojo.DynamicKafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ConsumerManager implements BeanFactoryAware {
    private DefaultListableBeanFactory beanFactory;

    KafkaProperties.Consumer consumer = new KafkaProperties.Consumer();
    private final String beanNameFormat = "%sKafkaMessageListenerContainer";

    private final Map<String, ConcurrentMessageListenerContainer> liveRegistry = new ConcurrentHashMap<>();

    /**
     * @param
     */
    public synchronized void process(DynamicKafkaProperties dynamicKafkaProperties) {
        if (!CollectionUtils.isEmpty(dynamicKafkaProperties.getConfig())) {
            dynamicKafkaProperties.getConfig().forEach((k, v) -> {
                try {
                    if (v.isEnabled()) {
                        log.info("consumer container starting......... {}", v.getName());
                        ContainerProperties containerProperties = new ContainerProperties(v.getTopics());
                        containerProperties.setGroupId(v.getGroupId());
                        ConsumerFactory consumerFactory = getConsumerFactory(v, dynamicKafkaProperties.getGlobalConfig());
                        containerProperties.setMessageListener(beanFactory.getBean(v.getListenerBeanName()));
                        String listenerContainerBeanName = String.format(beanNameFormat, v.getName());
                        if (existsBean(listenerContainerBeanName)) {
                            ConcurrentMessageListenerContainer messageListenerContainer = (ConcurrentMessageListenerContainer) beanFactory.getBean(listenerContainerBeanName);
                            if (!messageListenerContainer.isRunning()) {
                                messageListenerContainer.start();
                            }
                        } else {
                            registerAndStartContainer(consumerFactory, containerProperties, v);
                        }
                        ConcurrentMessageListenerContainer container = (ConcurrentMessageListenerContainer) beanFactory.getBean(listenerContainerBeanName);
                        liveRegistry.put(v.getName(), container);
                        log.info("consumer container started name->{}", v.getName());
                    } else {

                    }
                } catch (Exception e) {
                    log.error("consumer container name ->{} error->{}", v.getName(), e);
                }
            });
        }
        List<String> liveConsumerNames = dynamicKafkaProperties.getConfig().values().stream().filter(k -> k.isEnabled()).map(d -> d.getName()).distinct().collect(Collectors.toList());
        List<String> needDestroy = liveRegistry.keySet().stream().filter(e -> !liveConsumerNames.contains(e)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(needDestroy)) {
            for (String s : needDestroy) {
                try {
                    stopConsumer(s);
                    liveRegistry.remove(s);
                    log.info("consumer container stopped name->{}", s);
                } catch (Exception e) {
                    log.error("stop consumer error->{}", e);
                }
            }
        }
    }

    /**
     * @param beanName
     * @return
     */
    private boolean existsBean(String beanName) {
        return beanFactory.containsBean(beanName);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    /**
     * @param name
     */
    private void stopConsumer(String name) {
        if (!StringUtils.isEmpty(name)) {
            String listenerContainerBeanName = String.format(beanNameFormat, name);
            ConcurrentMessageListenerContainer concurrentMessageListenerContainer = beanFactory.getBean(listenerContainerBeanName, ConcurrentMessageListenerContainer.class);
            if (Objects.nonNull(concurrentMessageListenerContainer)) {
                concurrentMessageListenerContainer.stop();
                beanFactory.destroySingleton(listenerContainerBeanName);
                beanFactory.removeBeanDefinition(listenerContainerBeanName);
            }
            log.info("shutdownKafkaConsumer:{}", listenerContainerBeanName);
        }
    }


    /**
     * @param singleConfig
     * @param globalConfig
     * @return
     */
    private ConsumerFactory getConsumerFactory(DynamicKafkaProperties.SingleConfig singleConfig, Map<String, String> globalConfig) {
        Map<String, Object> totalProps = new HashMap<>();
        totalProps.putAll(consumer.buildProperties());
        if (!CollectionUtils.isEmpty(globalConfig)) {
            totalProps.putAll(globalConfig);
        }
        totalProps.putAll(singleConfig.getProps());
        totalProps.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, singleConfig.getBootstrapServers());
        return new DefaultKafkaConsumerFactory<>(totalProps);
    }


    /**
     * @param consumerFactory
     * @param properties
     * @param singleConfig
     */
    private void registerAndStartContainer(ConsumerFactory consumerFactory, ContainerProperties properties, DynamicKafkaProperties.SingleConfig singleConfig) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ConcurrentMessageListenerContainer.class);
        beanDefinitionBuilder.addConstructorArgValue(consumerFactory);
        beanDefinitionBuilder.addConstructorArgValue(properties);
        beanDefinitionBuilder.addPropertyValue("concurrency", singleConfig.getConcurrency());
        beanDefinitionBuilder.addPropertyValue("autoStartup", Boolean.TRUE);
        String beanName = String.format(beanNameFormat, singleConfig.getName());
        beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        ConcurrentMessageListenerContainer messageListenerContainer = (ConcurrentMessageListenerContainer) beanFactory.getBean(beanName);
        messageListenerContainer.start();
    }
}

