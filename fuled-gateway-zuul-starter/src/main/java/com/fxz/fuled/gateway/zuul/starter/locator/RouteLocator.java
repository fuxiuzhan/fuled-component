package com.fxz.fuled.gateway.zuul.starter.locator;

import com.fxz.fuled.gateway.zuul.starter.properties.RoutesProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator, ApplicationListener<ApplicationEvent> {

    private ZuulProperties zuulProperties;

    private RoutesProperties zuulRoutesProperties;

    public RouteLocator(String servletPath, ZuulProperties properties, RoutesProperties zuulRoutesProperties) {
        super(servletPath, properties);
        this.zuulProperties = properties;
        this.zuulRoutesProperties = zuulRoutesProperties;
        log.info("servletPath:{}", servletPath);
    }

    @Override
    public void refresh() {
        super.doRefresh();
    }

    @Override
    protected Map<String, ZuulRoute> locateRoutes() {
        Map<String, ZuulRoute> routesMap = locateRoutesFromProperties();
        if (CollectionUtils.isEmpty(routesMap)) {
            routesMap = super.locateRoutes();
        }
        LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.hasText(this.zuulProperties.getPrefix())) {
                path = this.zuulProperties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    protected Map<String, ZuulRoute> locateRoutesFromProperties() {
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();

        List<ZuulRoute> zuulRouteList = zuulRoutesProperties.getZuulRoutes();
        if (CollectionUtils.isEmpty(zuulRouteList)) {
            return routes;
        }
        zuulRouteList.stream().filter(zuulRoute -> StringUtils.hasText(zuulRoute.getPath())).forEach(zuulRoute -> {
            routes.put(zuulRoute.getPath(), zuulRoute);
        });
        return routes;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if ("ConfigChangeEvent".equals(event.getClass().getSimpleName())) {
            doRefresh();
        }
    }
}
