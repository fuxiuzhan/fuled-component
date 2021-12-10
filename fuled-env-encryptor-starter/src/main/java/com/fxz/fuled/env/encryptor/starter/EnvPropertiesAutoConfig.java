package com.fxz.fuled.env.encryptor.starter;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvPropertiesAutoConfig  implements EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Bean
    BeanFactoryPostProcessor testPro() {
        return new EnvPropertiesProcessor(environment);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}