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
package com.fxz.fuled.config.starter.annotation;


import com.fxz.fuled.config.starter.Config;
import com.fxz.fuled.config.starter.ConfigService;
import com.fxz.fuled.config.starter.model.ConfigChangeEvent;
import com.fxz.fuled.config.starter.spring.ConfigChangeListener;
import com.fxz.fuled.config.starter.spring.property.PlaceholderHelper;
import com.fxz.fuled.config.starter.spring.property.SpringValueRegistry;
import com.fxz.fuled.config.starter.spring.util.ConfigUtil;
import com.fxz.fuled.config.starter.spring.util.SpringInjector;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Apollo Annotation Processor for Spring Application
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DimaondAnnotationProcessor extends DimaondProcessor implements BeanFactoryAware,
        EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(DimaondAnnotationProcessor.class);
    private static final Gson GSON = new Gson();

    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;

    /**
     * resolve the expression.
     */
    private ConfigurableBeanFactory configurableBeanFactory;

    private Environment environment;

    public DimaondAnnotationProcessor() {
        placeholderHelper = SpringInjector.getInstance(PlaceholderHelper.class);
        springValueRegistry = SpringInjector.getInstance(SpringValueRegistry.class);
    }

    @Override
    protected void processField(Object bean, String beanName, Field field) {
        this.processApolloConfig(bean, field);
    }

    @Override
    protected void processMethod(final Object bean, String beanName, final Method method) {
        this.processApolloConfigChangeListener(bean, method);
    }

    private void processApolloConfig(Object bean, Field field) {
        DimaondConfig annotation = AnnotationUtils.getAnnotation(field, DimaondConfig.class);
        if (annotation == null) {
            return;
        }

        Preconditions.checkArgument(Config.class.isAssignableFrom(field.getType()),
                "Invalid type: %s for field: %s, should be Config", field.getType(), field);

        final String namespace = annotation.value();
        final String resolvedNamespace = this.environment.resolveRequiredPlaceholders(namespace);
        Config config = ConfigService.getConfig(resolvedNamespace);

        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, config);
    }

    private void processApolloConfigChangeListener(final Object bean, final Method method) {
        DimaondConfigChangeListener annotation = AnnotationUtils
                .findAnnotation(method, DimaondConfigChangeListener.class);
        if (annotation == null) {
            return;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Preconditions.checkArgument(parameterTypes.length == 1,
                "Invalid number of parameters: %s for method: %s, should be 1", parameterTypes.length,
                method);
        Preconditions.checkArgument(ConfigChangeEvent.class.isAssignableFrom(parameterTypes[0]),
                "Invalid parameter type: %s for method: %s, should be ConfigChangeEvent", parameterTypes[0],
                method);

        ReflectionUtils.makeAccessible(method);
        String[] namespaces = annotation.value();
        String[] annotatedInterestedKeys = annotation.interestedKeys();
        String[] annotatedInterestedKeyPrefixes = annotation.interestedKeyPrefixes();
        ConfigChangeListener configChangeListener = changeEvent -> ReflectionUtils.invokeMethod(method, bean, changeEvent);

        Set<String> interestedKeys =
                annotatedInterestedKeys.length > 0 ? Sets.newHashSet(annotatedInterestedKeys) : null;
        Set<String> interestedKeyPrefixes =
                annotatedInterestedKeyPrefixes.length > 0 ? Sets.newHashSet(annotatedInterestedKeyPrefixes)
                        : null;

//        for (String namespace : namespaces) {
//        final String resolvedNamespace = this.environment.resolveRequiredPlaceholders(ConfigUtil.APP_ID);
        ConfigUtil.initialize();
        final String resolvedNamespace = ConfigUtil.getAppId();
        Config config = ConfigService.getConfig(resolvedNamespace);
        if (interestedKeys == null && interestedKeyPrefixes == null) {
            config.addChangeListener(configChangeListener);
        } else {
            config.addChangeListener(configChangeListener, interestedKeys, interestedKeyPrefixes);
        }
//        }
    }

    private Object parseJsonValue(String json, Type targetType) {
        try {
            return GSON.fromJson(json, targetType);
        } catch (Throwable ex) {
            logger.error("Parsing json '{}' to type {} failed!", json, targetType, ex);
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
