/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.fxz.fuled.config.starter.spi;


import com.fxz.fuled.config.starter.annotation.DiamondAnnotationProcessor;
import com.fxz.fuled.config.starter.annotation.EnableDiamondConfig;
import com.fxz.fuled.config.starter.annotation.SpringValueProcessor;
import com.fxz.fuled.config.starter.nacos.config.NacosConfigBootstrapConfiguration;
import com.fxz.fuled.config.starter.spring.property.AutoUpdateConfigChangeListener;
import com.fxz.fuled.config.starter.spring.property.SpringValueDefinitionProcessor;
import com.fxz.fuled.config.starter.spring.util.ApplicationContextUtil;
import com.fxz.fuled.config.starter.spring.util.BeanRegistrationUtil;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;

public class DefaultApolloConfigRegistrarHelper implements ApolloConfigRegistrarHelper {

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(EnableDiamondConfig.class.getName()));
        final String[] namespaces = attributes.getStringArray("value");
        Map<String, Object> propertySourcesPlaceholderPropertyValues = new HashMap<String, Object>();
        // to make sure the default PropertySourcesPlaceholderConfigurer's priority is higher than PropertyPlaceholderConfigurer
        propertySourcesPlaceholderPropertyValues.put("order", 0);

        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesPlaceholderConfigurer.class.getName(),
                PropertySourcesPlaceholderConfigurer.class, propertySourcesPlaceholderPropertyValues);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, DiamondAnnotationProcessor.class.getName(),
                DiamondAnnotationProcessor.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueProcessor.class.getName(),
                SpringValueProcessor.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueDefinitionProcessor.class.getName(),
                SpringValueDefinitionProcessor.class);
//        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ServerAddrPropertySourceLocator.class.getName(),ServerAddrPropertySourceLocator.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, NacosConfigBootstrapConfiguration.class.getName(), NacosConfigBootstrapConfiguration.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, AutoUpdateConfigChangeListener.class.getName(), AutoUpdateConfigChangeListener.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApplicationContextUtil.class.getName(), ApplicationContextUtil.class);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
