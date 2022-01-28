package com.fxz.component.fuled.cat.starter.configuration;

import com.dianping.cat.servlet.CatFilter;
import com.fxz.component.fuled.cat.starter.component.http.HttpCatAfterFilter;
import com.fxz.component.fuled.cat.starter.component.http.HttpCatCrossFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatFilterConfiguration {
    @Value("#{'${cat.filter.url-patterns:/*,}'.replaceAll('[ \\n\\t]', '').split(',', 0)}")
    private String[] catFilterUrlPatterns;

    @Bean
    public FilterRegistrationBean catRemoteCallFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        HttpCatCrossFilter filter = new HttpCatCrossFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns(this.catFilterUrlPatterns);
        registration.setName("cat-call-http");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CatFilter filter = new CatFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns(this.catFilterUrlPatterns);
        registration.setName("cat-http");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    @ConditionalOnClass(name = {"org.slf4j.MDC"})
    public FilterRegistrationBean catAfterFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        HttpCatAfterFilter filter = new HttpCatAfterFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns(this.catFilterUrlPatterns);
        registration.setName("cat-after-http");
        registration.setOrder(2);
        return registration;
    }
}
