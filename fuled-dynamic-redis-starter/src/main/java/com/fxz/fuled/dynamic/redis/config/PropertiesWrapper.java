package com.fxz.fuled.dynamic.redis.config;


import com.fxz.fuled.dynamic.redis.properties.DynamicProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 对DynamicProperties 进行注册
 * 在参数变动是可以重新绑定参数
 * 但是链接无法动态替换，需要重启
 */
public class PropertiesWrapper implements ApplicationContextAware {

    @Autowired(required = false)
    private ConfigurationPropertiesBeans configurationPropertiesBeans;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, DynamicProperties> beansOfType = applicationContext.getBeansOfType(DynamicProperties.class);
        if (!CollectionUtils.isEmpty(beansOfType)) {
            beansOfType.forEach((k, v) -> {
                if (Objects.nonNull(configurationPropertiesBeans)) {
                    configurationPropertiesBeans.postProcessBeforeInitialization(v, k);
                }
            });
        }
    }
}
