package com.fxz.fuled.dynamic.kafka.config;

import com.fxz.fuled.common.chain.FilterChainManger;
import com.fxz.fuled.common.chain.FilterConfig;
import com.fxz.fuled.common.chain.Invoker;
import com.fxz.fuled.dynamic.kafka.filters.LoadConfigFromDBPropFilter;
import com.fxz.fuled.dynamic.kafka.manager.ConsumerManager;
import com.fxz.fuled.dynamic.kafka.pojo.DynamicKafkaProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import java.util.Objects;

@EnableConfigurationProperties({DynamicKafkaProperties.class})
@Import({FilterConfig.class, ConsumerManager.class, FilterChainManger.class, LoadConfigFromDBPropFilter.class})
public class DynamicKafkaAutoConfig implements ApplicationContextAware, SmartInitializingSingleton {
    public static final String DYNAMIC_KAFKA_FILTER_GROUP = "Dynamic_Kafka_Group";
    @Autowired
    private ConsumerManager consumerManager;
    @Autowired
    private FilterChainManger filterChainManger;
    private ApplicationContext applicationContext;
    private Invoker<DynamicKafkaProperties, Void> invoker;

    @Bean(name = "dynamicKafkaInvoker")
    public Invoker<DynamicKafkaProperties, Void> buildInvoker() {
        invoker = filterChainManger.getInvoker(DYNAMIC_KAFKA_FILTER_GROUP, (Invoker<DynamicKafkaProperties, Void>) dynamicKafkaProperties -> {
            consumerManager.process(dynamicKafkaProperties);
            return null;
        });
        return invoker;
    }

    @EventListener
    public void configChangeListener(ApplicationEvent event) {
        if (event instanceof EnvironmentChangeEvent) {
            DynamicKafkaProperties dynamicKafkaProperties = applicationContext.getBean(DynamicKafkaProperties.class);
            dynamicKafkaProperties.getConfig().clear();
            dynamicKafkaProperties.getGlobalConfig().clear();
            Binder.get(applicationContext.getEnvironment()).bind(DynamicKafkaProperties.PREFIX, Bindable.ofInstance(dynamicKafkaProperties));
            if (Objects.nonNull(invoker)) {
                invoker.invoke(dynamicKafkaProperties);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        DynamicKafkaProperties dynamicKafkaProperties = applicationContext.getBean(DynamicKafkaProperties.class);
        Binder.get(applicationContext.getEnvironment()).bind(DynamicKafkaProperties.PREFIX, Bindable.ofInstance(dynamicKafkaProperties));
        if (Objects.nonNull(invoker)) {
            invoker.invoke(dynamicKafkaProperties);
        }
    }
}
