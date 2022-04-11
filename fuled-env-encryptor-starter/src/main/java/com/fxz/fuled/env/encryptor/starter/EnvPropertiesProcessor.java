package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.converter.ValueConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.*;

import java.util.Objects;

/**
 * @author fxz
 * 将已有的properties进行包装代理，来实现加解密等二次操作
 * 与配置中心完全解耦
 */
public class EnvPropertiesProcessor implements BeanFactoryPostProcessor {
    private ConfigurableEnvironment environment;

    private ValueConverter valueConverter;

    public EnvPropertiesProcessor(Environment environment, ValueConverter valueConverter) {
        this.environment = (ConfigurableEnvironment) environment;
        this.valueConverter = valueConverter;
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
                wrapper = new EncryptablePropertySource(ps.getName(), ps, valueConverter);
            }
            if (ps instanceof EnumerablePropertySource) {
                wrapper = new EncryptablePropertySource(ps.getName(), ps, valueConverter);
            }
            if (Objects.nonNull(wrapper)) {
                propertySources.replace(ps.getName(), wrapper);
            }
        });
    }

}
