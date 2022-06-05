package com.fxz.component.fuled.cat.starter.configuration;

import com.fxz.component.fuled.cat.starter.annotation.CatTracing;
import com.fxz.component.fuled.cat.starter.component.feign.CatFeignAdvisor;
import com.fxz.component.fuled.cat.starter.custom.CatCustomAdvisor;
import com.fxz.component.fuled.cat.starter.mark.Mark;
import com.fxz.component.fuled.cat.starter.util.CatAspectUtil;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author fuled
 */
@Configuration
@Import({CatAspectUtil.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnBean(Mark.MarkClass.class)
public class CatAspectConfiguration {

    @Bean({"catCustomAdvisorService"})
    public CatCustomAdvisor catCustomAdvisorService() {
        CatCustomAdvisor advisor = new CatCustomAdvisor(Service.class);
        return advisor;
    }

    @Bean({"catCustomAdvisorComponent"})
    public CatCustomAdvisor catCustomAdvisorComponent() {
        CatCustomAdvisor advisor = new CatCustomAdvisor(Component.class);
        return advisor;
    }

    @Bean({"catCustomAdvisorRepository"})
    public CatCustomAdvisor catCustomAdvisorRepository() {
        CatCustomAdvisor advisor = new CatCustomAdvisor(Repository.class);
        return advisor;
    }

    @Bean({"catCustomAdvisorCatTracing"})
    public CatCustomAdvisor catCustomAdvisorCatTracing() {
        CatCustomAdvisor advisor = new CatCustomAdvisor(CatTracing.class, CatTracing.class);
        return advisor;
    }

    @Bean
    @ConditionalOnClass(name = {"org.springframework.cloud.openfeign.FeignClient"})
    public CatFeignAdvisor monitoringFeignClientAdvisor() {
        CatFeignAdvisor advisor = new CatFeignAdvisor(new AnnotationMatchingPointcut(FeignClient.class, true));
        return advisor;
    }
}
