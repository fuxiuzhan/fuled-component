package com.fxz.fuled.common.chain;


import com.fxz.fuled.common.common.FilterProperty;
import com.fxz.fuled.common.common.FilterType;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 管理filter列表
 *
 * @author fxz
 */
@Slf4j
public class FilterConfig {

    @Autowired
    List<Filter> allFilters;

    private List<Filter> preFilters = new ArrayList<>();
    private List<Filter> postFilters = new ArrayList<>();
    private List<Filter> storageFilters = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (allFilters != null && allFilters.size() > 0) {
            for (int i = 0; i < allFilters.size(); i++) {
                Filter filter = allFilters.get(i);
                FilterProperty annotation = filter.getClass().getAnnotation(FilterProperty.class);
                if (annotation != null && annotation.enabled()) {
                    log.info("Filter:name->{},order->{},type->{} added...", annotation.name(), annotation.order(), annotation.type());
                    switch (annotation.type()) {
                        case PRE:
                            preFilters.add(filter);
                            break;
                        case POST:
                            postFilters.add(filter);
                            break;
                        case STORAGE:
                            storageFilters.add(filter);
                            break;
                    }
                } else {
                    log.warn("filter->{},unknow filter type ,skiped....", new Gson().toJson(filter));
                }
            }
        }
    }

    public List<Filter> getFiltersByType(FilterType filterType) {
        if (filterType != null) {
            switch (filterType) {
                case PRE:
                    return sort(preFilters);
                case POST:
                    return sort(postFilters);
                case STORAGE:
                    return sort(storageFilters);
            }
        }
        return new ArrayList<>();
    }

    public List<Filter> sort(List<Filter> filters) {
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
