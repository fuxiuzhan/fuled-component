package com.fxz.fuled.common.cache.filters;

import com.fxz.fuled.common.cache.filters.abs.AbsCacheFilter;
import com.fxz.fuled.common.cache.objects.CacheIn;
import com.fxz.fuled.common.cache.objects.CacheOut;
import com.fxz.fuled.common.chain.Invoker;

import static com.fxz.fuled.common.cache.config.Constant.CACHE_GROUP_NAME;


public class ProceedCacheFilter extends AbsCacheFilter {
    private static final String NAME = "ProceedCacheFilter";

    public ProceedCacheFilter() {
        super(null);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String filterGroup() {
        return CACHE_GROUP_NAME;
    }

    @Override
    public CacheOut filter(CacheIn cacheIn, Invoker invoker) {
        CacheOut cacheOut = new CacheOut();
        try {
            Object proceed = cacheIn.getProceedingJoinPoint().proceed();
            cacheOut.setObject(proceed);
            cacheOut.setLastAccessTime(System.currentTimeMillis());
            return cacheOut;
        } catch (Throwable e) {
            cacheOut.setThrowable(e);
            cacheOut.setHasError(Boolean.TRUE);
        }
        return cacheOut;
    }
}
