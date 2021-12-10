package com.fxz.fuled.env.encryptor.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.*;

import java.util.Objects;

public class EnvPropertiesProcessor implements BeanFactoryPostProcessor {
    private ConfigurableEnvironment environment;

    public EnvPropertiesProcessor(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.forEach(ps -> {
            EncryptablePropertySource wrapper = null;
            //Some Spring Boot code actually casts property sources to this specific type so must be proxied.
            //for example
            //org.springframework.boot.context.config.ConfigFileApplicationListener$ConfigurationPropertySources
            if (ps instanceof MapPropertySource) {
                wrapper = new EncryptablePropertySource(ps.getName(), ps);
            }
            if (ps instanceof EnumerablePropertySource) {
                wrapper = new EncryptablePropertySource(ps.getName(), ps);
            }
            if (Objects.nonNull(wrapper)) {
                propertySources.replace(ps.getName(), wrapper);
            }
        });
    }

}
