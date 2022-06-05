package com.fxz.fuled.env.encryptor.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

/**
 * @author fxz
 * 将已有的properties进行包装代理，来实现加解密等二次操作
 * 与配置中心完全解耦
 */
public class EnvPropertiesProcessor implements BeanFactoryPostProcessor, Ordered {
    private ConfigurableEnvironment environment;


    private PropertiesConvertor propertiesConvertor;

    public EnvPropertiesProcessor(Environment environment, PropertiesConvertor propertiesConvertor) {
        this.environment = (ConfigurableEnvironment) environment;
        this.propertiesConvertor = propertiesConvertor;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertiesConvertor.convert(propertySources);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
