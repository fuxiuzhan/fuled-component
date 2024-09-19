package com.fxz.fuled.dynamic.kafka.n;

import com.fxz.fuled.common.chain.Filter;
import com.fxz.fuled.common.chain.FilterChainManger;
import com.fxz.fuled.common.chain.FilterConfig;
import com.fxz.fuled.common.chain.Invoker;
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

import java.util.List;
import java.util.Objects;

@EnableConfigurationProperties({DynamicKafkaProperties.class})
@Import({FilterConfig.class, ConsumerManager.class})
public class DynamicKafkaAutoConfig implements ApplicationContextAware, SmartInitializingSingleton {

    public static final String RAW_FILTER_GROUP = "Dynamic_Kafka_Group";
    @Autowired
    private ConsumerManager consumerManager;

    @Autowired
    private FilterConfig filterConfig;

    private ApplicationContext applicationContext;
    private Invoker<DynamicKafkaProperties, Void> invoker;

    @Bean(name = "dynamicKafkaInvoker")
    public Invoker<DynamicKafkaProperties, Void> buildInvoker() {
        FilterChainManger filterChainManger = new FilterChainManger();
        List<Filter> filtersByGroup = filterConfig.getFiltersByGroup(RAW_FILTER_GROUP);
        invoker = filterChainManger.buildInvokerChain((Invoker<DynamicKafkaProperties, Void>) dynamicKafkaProperties -> {
            consumerManager.process(dynamicKafkaProperties);
            return null;
        }, filtersByGroup);
        return invoker;
    }

    @EventListener
    public void configChangeListener(ApplicationEvent event) {
        if (event instanceof EnvironmentChangeEvent) {
            //原始properties
            DynamicKafkaProperties riskKafkaContainerProperties = applicationContext.getBean(DynamicKafkaProperties.class);
            Binder.get(applicationContext.getEnvironment()).bind(DynamicKafkaProperties.PREFIX, Bindable.ofInstance(riskKafkaContainerProperties));
            //新properties
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
