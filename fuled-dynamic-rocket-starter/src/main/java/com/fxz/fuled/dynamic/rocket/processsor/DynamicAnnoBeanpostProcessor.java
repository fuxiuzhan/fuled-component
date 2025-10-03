package com.fxz.fuled.dynamic.rocket.processsor;

import com.fxz.fuled.dynamic.rocket.anno.DynamicMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListenerBeanPostProcessor;
import org.apache.rocketmq.spring.autoconfigure.ListenerContainerConfiguration;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class DynamicAnnoBeanpostProcessor implements ApplicationContextAware, BeanPostProcessor, InitializingBean {

    private ListenerContainerConfiguration listenerContainerConfiguration;
    private ApplicationContext applicationContext;
    private RocketMQMessageListenerBeanPostProcessor.AnnotationEnhancer enhancer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        DynamicMessageListener dynaAnn = targetClass.getAnnotation(DynamicMessageListener.class);
        if (dynaAnn != null) {
            for (RocketMQMessageListener listener : dynaAnn.listeners()) {
                RocketMQMessageListener enhance = enhance(targetClass, listener);
                if (listenerContainerConfiguration != null) {
                    listenerContainerConfiguration.registerContainer(beanName, bean, enhance);
                }
            }
        }
        return bean;
    }

    private RocketMQMessageListener enhance(AnnotatedElement element, RocketMQMessageListener ann) {
        if (this.enhancer == null) {
            return ann;
        } else {
            return AnnotationUtils.synthesizeAnnotation(
                    this.enhancer.apply(AnnotationUtils.getAnnotationAttributes(ann), element), RocketMQMessageListener.class, null);
        }
    }

    @Override
    public void afterPropertiesSet() {
        buildEnhancer();
        this.listenerContainerConfiguration = this.applicationContext.getBean(ListenerContainerConfiguration.class);
    }

    private void buildEnhancer() {
        if (this.applicationContext != null) {
            Map<String, RocketMQMessageListenerBeanPostProcessor.AnnotationEnhancer> enhancersMap =
                    this.applicationContext.getBeansOfType(RocketMQMessageListenerBeanPostProcessor.AnnotationEnhancer.class, false, false);
            if (enhancersMap.size() > 0) {
                List<RocketMQMessageListenerBeanPostProcessor.AnnotationEnhancer> enhancers = enhancersMap.values()
                        .stream()
                        .sorted(new OrderComparator())
                        .collect(Collectors.toList());
                this.enhancer = (attrs, element) -> {
                    Map<String, Object> newAttrs = attrs;
                    for (RocketMQMessageListenerBeanPostProcessor.AnnotationEnhancer enh : enhancers) {
                        newAttrs = enh.apply(newAttrs, element);
                    }
                    return attrs;
                };
            }
        }
    }
}
