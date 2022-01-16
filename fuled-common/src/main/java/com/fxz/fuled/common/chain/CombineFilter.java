package com.fxz.fuled.common.chain;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author fxz
 */
public class CombineFilter<T> implements Filter<T> {
    private List<Filter> filters;
    private Executor executor;

    @Override
    public <R> R filter(T t, Invoker invoker) {
        filters.forEach(f -> {
            if (executor != null) {
                executor.execute(() -> f.filter(t, invoker));
            } else {
                f.filter(t, invoker);
            }
        });
        return null;
    }

    public CombineFilter(List<Filter> filters) {
        this.filters = filters;

    }

    public CombineFilter(List<Filter> filters, Executor executor) {
        this.filters = filters;
        this.executor = executor;
    }

}
