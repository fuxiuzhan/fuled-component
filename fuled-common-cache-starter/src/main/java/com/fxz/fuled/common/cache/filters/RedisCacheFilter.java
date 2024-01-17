package com.fxz.fuled.common.cache.filters;

import com.fxz.fuled.common.cache.container.CacheContainer;
import com.fxz.fuled.common.cache.filters.abs.AbsCacheFilter;

public class RedisCacheFilter extends AbsCacheFilter {
    private static final String NAME = "RedisCacheFilter";

    public RedisCacheFilter(CacheContainer cacheContainer) {
        super(cacheContainer);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE >> 4;
    }
}
