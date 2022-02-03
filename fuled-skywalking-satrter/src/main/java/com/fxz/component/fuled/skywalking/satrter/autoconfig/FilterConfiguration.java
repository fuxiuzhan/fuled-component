package com.fxz.component.fuled.skywalking.satrter.autoconfig;

import com.fxz.component.fuled.skywalking.satrter.webfilter.TraceIdFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author fxz
 */
@Configuration
@ConditionalOnWebApplication
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean swTraceFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        TraceIdFilter filter = new TraceIdFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("*");
        registration.setName("skywalking-filter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
