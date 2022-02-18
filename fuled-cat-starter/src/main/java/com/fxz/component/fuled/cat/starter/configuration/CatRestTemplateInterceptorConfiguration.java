package com.fxz.component.fuled.cat.starter.configuration;


import com.fxz.component.fuled.cat.starter.component.template.rest.CatRestTemplateInterceptor;
import com.fxz.component.fuled.cat.starter.mark.Mark;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author fuled
 */
@Configuration
@ConditionalOnClass({RestTemplate.class})
@ConditionalOnBean(Mark.MarkClass.class)
public class CatRestTemplateInterceptorConfiguration implements BeanPostProcessor {
    public CatRestTemplateInterceptorConfiguration() {
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (o instanceof RestTemplate) {
            RestTemplate restTemplate = (RestTemplate) o;
            restTemplate.getInterceptors().add(new CatRestTemplateInterceptor());
        }
        return o;
    }
}
