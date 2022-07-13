package com.fxz.fuled.gateway.zuul.starter.config;

import com.fxz.fuled.gateway.zuul.starter.locator.RouteLocator;
import com.fxz.fuled.gateway.zuul.starter.properties.RoutesProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.ZuulServerAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AutoConfigureBefore(ZuulServerAutoConfiguration.class)
@EnableConfigurationProperties(RoutesProperties.class)
public class AutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public RouteLocator customRouteLocator(ServerProperties serverProperties, ZuulProperties zuulProperties, RoutesProperties routesProperties) {
        return new RouteLocator(serverProperties.getServlet().getContextPath(), zuulProperties, routesProperties);
    }
}
