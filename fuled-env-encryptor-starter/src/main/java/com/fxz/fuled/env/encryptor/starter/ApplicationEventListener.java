package com.fxz.fuled.env.encryptor.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;

/**
 * @author fxz
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@Slf4j
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent>, InitializingBean, Ordered {
    private ConfigurableEnvironment environment;
    private PropertiesConvertor propertiesConvertor;
    public static final List<String> EVENT_CLASS_NAMES = Arrays.asList(
            "org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent",
            "org.springframework.cloud.context.environment.EnvironmentChangeEvent",
            "org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent",
            "org.springframework.boot.context.event.ApplicationStartedEvent.ApplicationStartedEvent"
    );

    public ApplicationEventListener(ConfigurableEnvironment environment, PropertiesConvertor propertiesConvertor) {
        this.environment = environment;
        this.propertiesConvertor = propertiesConvertor;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //TODO 可以增加配置设置执行条件
        if (EVENT_CLASS_NAMES.contains(event.getClass().getName())) {
            log.info("eventType->{}", event.getClass().getName());
            propertiesConvertor.convert(environment.getPropertySources());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
