package com.fxz.fuled.env.encryptor.starter;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * @author fxz
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvPropertiesAutoConfig implements EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Bean
    @Order()
    BeanFactoryPostProcessor envPropertiesProcessor() {
        return new EnvPropertiesProcessor(environment);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}