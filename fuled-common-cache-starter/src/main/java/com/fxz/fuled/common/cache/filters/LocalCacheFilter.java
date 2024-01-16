package com.fxz.fuled.common.cache.filters;


import com.fxz.fuled.common.cache.container.CacheContainer;
import com.fxz.fuled.common.cache.filters.abs.AbsCacheFilter;
import com.fxz.fuled.common.cache.objects.CacheIn;
import com.fxz.fuled.common.cache.objects.CacheOut;
import com.fxz.fuled.common.chain.Invoker;

import static com.fxz.fuled.common.cache.config.Constant.CACHE_GROUP_NAME;


public class LocalCacheFilter extends AbsCacheFilter {

    private static final String NAME = "LocalCacheFilter";

    public LocalCacheFilter(CacheContainer cacheContainer) {
        super(cacheContainer);
    }

    @Override
    public CacheOut filter(CacheIn cacheIn, Invoker<CacheIn, CacheOut> invoker) {
        //
        if (cacheIn.getCaches().stream().filter(c -> c.localTurbo()).findFirst().isPresent()) {
            return super.filter(cacheIn, invoker);
        } else {
            return invoker.invoke(cacheIn);
        }
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String filterGroup() {
        return CACHE_GROUP_NAME;
    }
}
