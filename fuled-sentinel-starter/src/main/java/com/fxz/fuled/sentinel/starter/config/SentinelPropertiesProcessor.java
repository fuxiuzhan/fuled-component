package com.fxz.fuled.sentinel.starter.config;

import com.alibaba.cloud.sentinel.SentinelProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fxz
 * <p>
 * 此类要处理的东西
 * 1、改写sentinel服务的地址，可以将各个环境的地址统一管理，做到标准统一
 * 2、其实sentinel的properties在启动时创建完transport后即使在修改也不会生效，不过此处还是需要将properties进行注册
 */
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
@Component
public class SentinelPropertiesProcessor extends AutowiredAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private AtomicBoolean inited = new AtomicBoolean(Boolean.FALSE);
    private BeanDefinitionRegistry registry;
    private ConfigurableListableBeanFactory beanFactory;

    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private ConfigurationPropertiesBeans configurationPropertiesBeans;

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        if (inited.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            /**
             * 拿到primary就可以了。
             */
            SentinelProperties sentinelProperties = beanFactory.getBean(SentinelProperties.class);
            if (Objects.nonNull(sentinelProperties)) {
                wrapperSentinelProperties(sentinelProperties);
            }
        }
        return super.postProcessProperties(pvs, bean, beanName);
    }

    private void wrapperSentinelProperties(SentinelProperties sentinelProperties) {
        if (Objects.nonNull(sentinelProperties)) {
            sentinelProperties.getTransport().setDashboard(SentinelEnv.getEnv().getDashboard());
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, SentinelProperties> beansOfType = applicationContext.getBeansOfType(SentinelProperties.class);
        if (!CollectionUtils.isEmpty(beansOfType)) {
            beansOfType.forEach((k, v) -> {
                if (Objects.nonNull(configurationPropertiesBeans)) {
                    configurationPropertiesBeans.postProcessBeforeInitialization(v, k);
                }
            });
        }
    }
}
