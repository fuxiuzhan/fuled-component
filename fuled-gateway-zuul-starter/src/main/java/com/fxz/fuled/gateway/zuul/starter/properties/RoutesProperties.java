package com.fxz.fuled.gateway.zuul.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ConfigurationProperties(prefix = "fuled.zuul")
public class RoutesProperties {

    private Map<String, CustomZuulRoute> routes;

    @Data
    public static class CustomZuulRoute extends ZuulProperties.ZuulRoute {
        private Integer order = 0;

    }

    public List<ZuulProperties.ZuulRoute> getZuulRoutes() {
        if (CollectionUtils.isEmpty(routes)) {
            return Collections.emptyList();
        }

        for (Map.Entry<String, CustomZuulRoute> entry : this.routes.entrySet()) {
            CustomZuulRoute value = entry.getValue();
            if (!StringUtils.hasText(value.getLocation())) {
                value.setServiceId(entry.getKey());
            }
            if (!StringUtils.hasText(value.getId())) {
                value.setId(entry.getKey());
            }
            if (!StringUtils.hasText(value.getPath())) {
                value.setPath("/" + entry.getKey() + "/**");
            }
        }
        List<CustomZuulRoute> collect = routes.entrySet().stream().map(et -> et.getValue()).collect(Collectors.toList());
        collect.sort(Comparator.comparing(CustomZuulRoute::getOrder));

        List<ZuulProperties.ZuulRoute> zuulRoute = collect.stream().map(customZuulRoute -> {
            return (ZuulProperties.ZuulRoute) customZuulRoute;
        }).collect(Collectors.toList());

        return zuulRoute;
    }

}
