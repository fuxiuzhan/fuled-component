package com.fxz.fuled.gateway.zuul.starter.config;

import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.gateway.zuul.starter.auth.AuthService;
import com.fxz.fuled.gateway.zuul.starter.filter.AccessFilter;
import com.fxz.fuled.gateway.zuul.starter.filter.AccessTokenFilter;
import com.fxz.fuled.gateway.zuul.starter.locator.RouteLocator;
import com.fxz.fuled.gateway.zuul.starter.pojo.JwtInfo;
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

    @Bean("gatewayZuulVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-gateway-zuul.version", "1.0.0.waterdrop", "fuled-gateway-zuul-component");
    }

    @Bean
    public AccessTokenFilter tokenFilter() {
        return new AccessTokenFilter();
    }

    @Bean
    public AccessFilter accessFilter() {
        return new AccessFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService() {
        return (token, url) -> {
            JwtInfo jwtInfo = new JwtInfo();
            jwtInfo.setUserName("sample");
            jwtInfo.setUserId("userId");
            jwtInfo.setAlias("userAlias");
            jwtInfo.setDept(url);
            return jwtInfo;
        };
    }
}
