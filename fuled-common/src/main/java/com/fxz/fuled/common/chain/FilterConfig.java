package com.fxz.fuled.common.chain;


import com.fxz.fuled.common.chain.annotation.FilterProperties;
import com.fxz.fuled.common.chain.annotation.FilterProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理filter列表
 *
 * @author fxz
 */
@Slf4j
@Component
public class FilterConfig {
    private Map<String, List<FilterWrapper>> filterMap = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private List<Filter> filters;

    @PostConstruct
    public void init() {
        if (!CollectionUtils.isEmpty(filters)) {
            for (Filter filter : filters) {
                FilterProperties annoProperties = filter.getClass().getAnnotation(FilterProperties.class);
                if (Objects.nonNull(annoProperties) && annoProperties.properties().length > 0) {
                    for (int i = 0; i < annoProperties.properties().length; i++) {
                        addSingleProperty(annoProperties.properties()[i], filter);
                    }
                }
                FilterProperty annoProperty = filter.getClass().getAnnotation(FilterProperty.class);
                if (Objects.nonNull(annoProperty)) {
                    addSingleProperty(annoProperty, filter);
                }
            }
            shakeFilterMap();
        }
    }

    /**
     * 处理单个filter
     * 单个filter 可能隶属于多个group
     *
     * @param filterProperty
     * @param filter
     */
    private void addSingleProperty(FilterProperty filterProperty, Filter filter) {
        log.info("Filter->{} group->{} order->{} enabled->{} adding....", filterProperty.name(), filterProperty.filterGroup(), filterProperty.order(), filterProperty.enabled());
        if (filterProperty.enabled()) {
            String group = filterProperty.filterGroup();
            List<FilterWrapper> orDefault = filterMap.getOrDefault(group, new ArrayList<>());
            orDefault.add(new FilterWrapper(filter, filterProperty.order()));
            filterMap.put(group, orDefault);
            log.info("Filter->{} group->{} order->{} enabled->{} added", filterProperty.name(), filterProperty.filterGroup(), filterProperty.order(), filterProperty.enabled());
        }
    }

    /**
     * 把filter容器摇匀
     */
    private void shakeFilterMap() {
        if (!CollectionUtils.isEmpty(filterMap)) {
            filterMap.forEach((k, v) -> v.sort((o1, o2) -> {
                if (o1.getOrder() == o2.getOrder()) {
                    return 0;
                }
                return o1.getOrder() - o2.getOrder();
            }));
        }
    }

    /**
     * 根据group 获取对应的filter列表，已经排好序
     *
     * @param group
     * @return
     */
    public List<Filter> getFiltersByGroup(String group) {
        List<FilterWrapper> orDefault = filterMap.getOrDefault(group, new ArrayList<>());
        return orDefault.stream().map(a -> a.filter).collect(Collectors.toList());
    }

    /**
     * Filter 展平工具
     */
    @Data
    public class FilterWrapper implements Filter {
        private Filter filter;
        private int order;

        public FilterWrapper(Filter filter, int order) {
            this.filter = filter;
            this.order = order;
        }

        @Override
        public Object filter(Object o, Invoker invoker) {
            return filter.filter(o, invoker);
        }
    }
}

