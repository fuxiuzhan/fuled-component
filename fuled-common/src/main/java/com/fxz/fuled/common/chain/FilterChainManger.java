package com.fxz.fuled.common.chain;


import com.fxz.fuled.common.common.FilterType;

import java.util.List;

/**
 * <p>
 * <p>
 * 流程详见上级接口说明
 */
public class FilterChainManger<T> implements FilterChain<T> {

    Invoker postInvoker = null;

    FilterConfig filterConfig;

    public FilterChainManger() {
    }

    public FilterChainManger(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.preFilters = filterConfig.getFiltersByType(FilterType.PRE);
        this.postFilters = filterConfig.getFiltersByType(FilterType.POST);
    }

    private List<Filter> preFilters;
    private List<Filter> postFilters;

    @Override
    public Invoker buildInvokerChain(Invoker invoker, List<Filter> filters) {
        Invoker head = invoker;
        if (filters != null && filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                Filter filter = filters.get(i);
                Invoker next = head;
                head = new Invoker() {
                    @Override
                    public Object invoke(Object o) {
                        return filter.filte(o, next);
                    }
                };
            }
        }
        return head;
    }
}
