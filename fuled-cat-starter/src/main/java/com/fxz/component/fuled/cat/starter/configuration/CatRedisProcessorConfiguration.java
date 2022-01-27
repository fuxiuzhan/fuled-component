package com.fxz.component.fuled.cat.starter.configuration;

import com.fxz.component.fuled.cat.starter.component.redis.RedisTemplateCatPlus;
import com.fxz.component.fuled.cat.starter.component.redis.StringRedisTemplateCatPlus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * @author fxz
 */
@Configuration
public class CatRedisProcessorConfiguration implements BeanPostProcessor {
    @Autowired
    private DefaultListableBeanFactory beanFactory;
    private static final String TARGET_BEAN_NAME = "redisTemplate";

    public CatRedisProcessorConfiguration() {
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (StringUtils.endsWithIgnoreCase(beanName, "redisTemplate") && this.beanFactory.containsBean("redisTemplate")) {
            RedisTemplate redisTemplate = null;
            if (bean.getClass().getSimpleName().equals(RedisTemplateCatPlus.class.getSimpleName())) {
                redisTemplate = (RedisTemplateCatPlus) bean;
                return redisTemplate;
            }

            if (bean.getClass().getSimpleName().equals(StringRedisTemplateCatPlus.class.getSimpleName())) {
                redisTemplate = (StringRedisTemplateCatPlus) bean;
                return redisTemplate;
            }

            if (bean.getClass().getSimpleName().equals(StringRedisTemplate.class.getSimpleName())) {
                redisTemplate = (StringRedisTemplate) bean;
                StringRedisTemplateCatPlus redisTemplatePlus = new StringRedisTemplateCatPlus();
                BeanUtils.copyProperties(redisTemplate, redisTemplatePlus);
                redisTemplatePlus.afterPropertiesSet();
                this.changeBeanHandle(beanName, StringRedisTemplateCatPlus.class);
                return redisTemplatePlus;
            }

            if (bean.getClass().getSimpleName().equals(RedisTemplate.class.getSimpleName())) {
                redisTemplate = (RedisTemplate) bean;
                RedisTemplateCatPlus redisTemplatePlus = new RedisTemplateCatPlus();
                BeanUtils.copyProperties(redisTemplate, redisTemplatePlus);
                redisTemplatePlus.afterPropertiesSet();
                this.changeBeanHandle(beanName, RedisTemplateCatPlus.class);
                return redisTemplatePlus;
            }
        }

        return bean;
    }

    private void changeBeanHandle(String targetBeanName, Class<?> beanClass) {
        this.beanFactory.removeBeanDefinition(targetBeanName);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinitionBuilder.addPropertyReference("connectionFactory", "redisConnectionFactory");
        this.beanFactory.registerBeanDefinition(targetBeanName, beanDefinition);
    }
}
