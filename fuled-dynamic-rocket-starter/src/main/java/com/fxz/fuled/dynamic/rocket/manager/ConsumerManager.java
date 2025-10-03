package com.fxz.fuled.dynamic.rocket.manager;

import com.fxz.fuled.dynamic.rocket.pojo.DynamicRocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ConsumerManager implements BeanFactoryAware, EnvironmentAware {
    private DefaultListableBeanFactory beanFactory;
    private Environment environment;

    @Autowired
    private RocketMQProperties rocketMQProperties;

    @Autowired
    private RocketMQMessageConverter rocketMQMessageConverter;
    private final String beanNameFormat = "%sRocketMqMessageListenerContainer";
    private final Map<String, DefaultRocketMQListenerContainer> liveRegistry = new ConcurrentHashMap<>();

    /**
     * @param
     */
    public synchronized void process(DynamicRocketProperties dynamicRocketProperties) {
        if (!CollectionUtils.isEmpty(dynamicRocketProperties.getConfig())) {
            dynamicRocketProperties.getConfig().forEach((k, v) -> {
                try {
                    if (v.isEnabled()) {
                        String listenerContainerBeanName = String.format(beanNameFormat, v.getName());
                        if (existsBean(listenerContainerBeanName)) {
                            DefaultRocketMQListenerContainer messageListenerContainer = (DefaultRocketMQListenerContainer) beanFactory.getBean(listenerContainerBeanName);
                            if (!messageListenerContainer.isRunning()) {
                                messageListenerContainer.start();
                            }
                        } else {
                            registerAndStartContainer(listenerContainerBeanName, v);
                            log.info("consumer container started name->{}", v.getName());
                        }
                        DefaultRocketMQListenerContainer container = (DefaultRocketMQListenerContainer) beanFactory.getBean(listenerContainerBeanName);
                        if (!container.isRunning()) {
                            container.start();
                            log.info("consumer container started name->{}", v.getName());
                        }
                        liveRegistry.put(v.getName(), container);
                    }
                } catch (Exception e) {
                    log.error("consumer container name ->{} error->{}", v.getName(), e);
                }
            });
        }
        List<String> liveConsumerNames = dynamicRocketProperties.getConfig().values().stream().filter(k -> k.isEnabled()).map(d -> d.getName()).distinct().collect(Collectors.toList());
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
            DefaultRocketMQListenerContainer concurrentMessageListenerContainer = beanFactory.getBean(listenerContainerBeanName, DefaultRocketMQListenerContainer.class);
            if (Objects.nonNull(concurrentMessageListenerContainer)) {
                concurrentMessageListenerContainer.stop();
                beanFactory.destroySingleton(listenerContainerBeanName);
                beanFactory.removeBeanDefinition(listenerContainerBeanName);
            }
            log.info("shutdownKafkaConsumer:{}", listenerContainerBeanName);
        }
    }

    /**
     * @param beanName
     * @param singleConfig
     */
    private void registerAndStartContainer(String beanName, DynamicRocketProperties.SingleConfig singleConfig) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultRocketMQListenerContainer.class);
        beanDefinitionBuilder.addPropertyValue("rocketMQMessageListener", buildAnnoFromProperties(singleConfig));
        String nameServer = environment.resolvePlaceholders(singleConfig.getNameServer());
        nameServer = StringUtils.hasLength(nameServer) ? nameServer : rocketMQProperties.getNameServer();
        String accessChannel = environment.resolvePlaceholders(singleConfig.getConsumer().getAccessChannel());
        beanDefinitionBuilder.addPropertyValue("nameServer", nameServer);
        if (StringUtils.hasLength(accessChannel)) {
            beanDefinitionBuilder.addPropertyValue("accessChannel", AccessChannel.valueOf(accessChannel));
        }
        beanDefinitionBuilder.addPropertyValue("topic", environment.resolvePlaceholders(singleConfig.getConsumer().getTopic()));
        String tags = environment.resolvePlaceholders(singleConfig.getConsumer().getSelectorExpression());
        if (StringUtils.hasLength(tags)) {
            beanDefinitionBuilder.addPropertyValue("selectorExpression", tags);
        }
        beanDefinitionBuilder.addPropertyValue("consumerGroup", singleConfig.getConsumer().getConsumerGroup());
        beanDefinitionBuilder.addPropertyValue("tlsEnable", environment.resolvePlaceholders(singleConfig.getConsumer().getTlsEnable()));
        Object lisetner = beanFactory.getBean(singleConfig.getListenerBeanName());
        if (RocketMQListener.class.isAssignableFrom(lisetner.getClass())) {
            beanDefinitionBuilder.addPropertyValue("rocketMQListener", (RocketMQListener) lisetner);
        } else if (RocketMQReplyListener.class.isAssignableFrom(lisetner.getClass())) {
            beanDefinitionBuilder.addPropertyValue("rocketMQReplyListener", (RocketMQReplyListener) lisetner);
        }
        beanDefinitionBuilder.addPropertyValue("messageConverter", rocketMQMessageConverter.getMessageConverter());
        beanDefinitionBuilder.addPropertyValue("name", String.format(beanNameFormat, singleConfig.getName()));
        String namespace = environment.resolvePlaceholders(singleConfig.getConsumer().getNamespace());
        beanDefinitionBuilder.addPropertyValue("namespace", RocketMQUtil.getNamespace(namespace,
                rocketMQProperties.getConsumer().getNamespace()));
        beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * @param singleConfig
     * @return
     */
    private RocketMQMessageListener buildAnnoFromProperties(DynamicRocketProperties.SingleConfig singleConfig) {
        return new RocketMQMessageListener() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return RocketMQMessageListener.class;
            }

            @Override
            public String consumerGroup() {
                return singleConfig.getConsumer().getConsumerGroup();
            }

            @Override
            public String topic() {
                return singleConfig.getConsumer().getTopic();
            }

            @Override
            public SelectorType selectorType() {
                return singleConfig.getConsumer().getSelectorType();
            }

            @Override
            public String selectorExpression() {
                return singleConfig.getConsumer().getSelectorExpression();
            }

            @Override
            public ConsumeMode consumeMode() {
                return singleConfig.getConsumer().getConsumeMode();
            }

            @Override
            public MessageModel messageModel() {
                return singleConfig.getConsumer().getMessageModel();
            }

            @Override
            public int consumeThreadMax() {
                return singleConfig.getConsumer().getConsumeThreadMax();
            }

            @Override
            public int consumeThreadNumber() {
                return singleConfig.getConsumer().getConsumeThreadNumber();
            }

            @Override
            public int maxReconsumeTimes() {
                return singleConfig.getConsumer().getMaxReconsumeTimes();
            }

            @Override
            public long consumeTimeout() {
                return singleConfig.getConsumer().getConsumeTimeout();
            }

            @Override
            public int replyTimeout() {
                return singleConfig.getConsumer().getReplyTimeout();
            }

            @Override
            public String accessKey() {
                return singleConfig.getConsumer().getAccessKey();
            }

            @Override
            public String secretKey() {
                return singleConfig.getConsumer().getSecretKey();
            }

            @Override
            public boolean enableMsgTrace() {
                return singleConfig.getConsumer().isEnableMsgTrace();
            }

            @Override
            public String customizedTraceTopic() {
                return singleConfig.getConsumer().getCustomizedTraceTopic();
            }

            @Override
            public String nameServer() {
                return singleConfig.getConsumer().getNameServer();
            }

            @Override
            public String accessChannel() {
                return singleConfig.getConsumer().getAccessChannel();
            }

            @Override
            public String tlsEnable() {
                return singleConfig.getConsumer().getTlsEnable();
            }

            @Override
            public String namespace() {
                return singleConfig.getConsumer().getNamespace();
            }

            @Override
            public int delayLevelWhenNextConsume() {
                return singleConfig.getConsumer().getDelayLevelWhenNextConsume();
            }

            @Override
            public int suspendCurrentQueueTimeMillis() {
                return singleConfig.getConsumer().getSuspendCurrentQueueTimeMillis();
            }

            @Override
            public int awaitTerminationMillisWhenShutdown() {
                return singleConfig.getConsumer().getAwaitTerminationMillisWhenShutdown();
            }

            @Override
            public String instanceName() {
                return singleConfig.getConsumer().getInstanceName();
            }
        };
    }
}

