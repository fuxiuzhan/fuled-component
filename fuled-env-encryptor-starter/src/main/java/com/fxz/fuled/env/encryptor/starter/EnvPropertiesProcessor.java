package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.converter.ValueConverter;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptableEnumerablePropertySourceWrapper;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptableMapPropertySourceWrapper;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptablePropertySource;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptablePropertySourceWrapper;
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
            if (!(ps instanceof EncryptablePropertySource)) {
                PropertySource propertySource = null;
                if (ps instanceof SystemEnvironmentPropertySource) {
                }
                if (ps instanceof PropertiesPropertySource) {
                    propertySource = new EncryptablePropertySourceWrapper<>(ps, valueConverter);
                }
                if (ps instanceof MapPropertySource) {
                    propertySource = new EncryptableMapPropertySourceWrapper(ps, valueConverter);
                }
                if (ps instanceof EnumerablePropertySource) {
                    propertySource = new EncryptableEnumerablePropertySourceWrapper<>(ps, valueConverter);
                }
                if (Objects.nonNull(propertySource)) {
                    propertySources.replace(ps.getName(), propertySource);
                }
            }
        });
    }

}
