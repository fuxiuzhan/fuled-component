package com.fxz.fuled.common.chain;


import com.fxz.fuled.common.common.FilterProperty;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理filter列表
 *
 * @author fxz
 */
@Slf4j
@Configuration
public class FilterConfig {

    @Autowired(required = false)
    List<Filter> allFilters;
    Map<String, List<Filter>> filterGroup = new HashMap<>();

    @PostConstruct
    public void init() {
        if (allFilters != null && allFilters.size() > 0) {
            for (int i = 0; i < allFilters.size(); i++) {
                Filter filter = allFilters.get(i);
                FilterProperty annotation = filter.getClass().getAnnotation(FilterProperty.class);
                if (annotation != null && annotation.enabled() && StringUtils.hasText(annotation.filterGroup())) {
                    log.info("Filter:name->{},group->{},order->{} added...", annotation.name(), annotation.filterGroup(), annotation.order());
                    List<Filter> filterList = filterGroup.get(annotation.filterGroup());
                    if (filterList == null) {
                        filterList = new ArrayList<>();
                    }
                    filterList.add(filter);
                    filterGroup.put(annotation.filterGroup(), filterList);
                } else {
                    log.warn("filter->{},unknown filter type ,skipped....", new Gson().toJson(filter));
                }
            }
        }
    }

    public List<Filter> getFiltersByName(String groupName) {
        if (StringUtils.hasText(groupName)) {
            return sort(filterGroup.get(groupName));
        }
        return new ArrayList<>();
    }

    private List<Filter> sort(List<Filter> filters) {
        if (filters == null || filters.size() == 0) {
            return new ArrayList<>();
        }
        filters.sort((o1, o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            FilterProperty annotation1 = o1.getClass().getAnnotation(FilterProperty.class);
            FilterProperty annotation2 = o2.getClass().getAnnotation(FilterProperty.class);
            return annotation1.order() - annotation2.order();
        });
        return filters;
    }
}
