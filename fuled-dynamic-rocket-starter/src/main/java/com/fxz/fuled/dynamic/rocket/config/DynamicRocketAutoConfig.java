package com.fxz.fuled.dynamic.rocket.config;

import com.fxz.fuled.common.chain.FilterChainManger;
import com.fxz.fuled.common.chain.FilterConfig;
import com.fxz.fuled.common.chain.Invoker;
import com.fxz.fuled.dynamic.rocket.filters.LoadConfigFromDBPropFilter;
import com.fxz.fuled.dynamic.rocket.manager.ConsumerManager;
import com.fxz.fuled.dynamic.rocket.pojo.DynamicRocketProperties;
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

@EnableConfigurationProperties({DynamicRocketProperties.class})
@Import({FilterConfig.class, ConsumerManager.class, FilterChainManger.class, LoadConfigFromDBPropFilter.class})
public class DynamicRocketAutoConfig implements ApplicationContextAware, SmartInitializingSingleton {
    public static final String DYNAMIC_ROCKET_FILTER_GROUP = "Dynamic_Rocket_Group";
    @Autowired
    private ConsumerManager consumerManager;
    @Autowired
    private FilterChainManger filterChainManger;
    private ApplicationContext applicationContext;
    private Invoker<DynamicRocketProperties, Void> invoker;

    @Bean(name = "dynamicRocketInvoker")
    public Invoker<DynamicRocketProperties, Void> buildInvoker() {
        invoker = filterChainManger.getInvoker(DYNAMIC_ROCKET_FILTER_GROUP, (Invoker<DynamicRocketProperties, Void>) dynamicKafkaProperties -> {
            consumerManager.process(dynamicKafkaProperties);
            return null;
        });
        return invoker;
    }

    @EventListener
    public void configChangeListener(ApplicationEvent event) {
        if (event instanceof EnvironmentChangeEvent) {
            DynamicRocketProperties dynamicKafkaProperties = applicationContext.getBean(DynamicRocketProperties.class);
            dynamicKafkaProperties.getConfig().clear();
            Binder.get(applicationContext.getEnvironment()).bind(DynamicRocketProperties.PREFIX, Bindable.ofInstance(dynamicKafkaProperties));
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
        DynamicRocketProperties dynamicKafkaProperties = applicationContext.getBean(DynamicRocketProperties.class);
        Binder.get(applicationContext.getEnvironment()).bind(DynamicRocketProperties.PREFIX, Bindable.ofInstance(dynamicKafkaProperties));
        if (Objects.nonNull(invoker)) {
            invoker.invoke(dynamicKafkaProperties);
        }
    }
}
