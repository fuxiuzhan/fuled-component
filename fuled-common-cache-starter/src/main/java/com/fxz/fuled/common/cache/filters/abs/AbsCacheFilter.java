package com.fxz.fuled.common.cache.filters.abs;


import com.fxz.fuled.common.cache.config.Constant;
import com.fxz.fuled.common.cache.container.CacheContainer;
import com.fxz.fuled.common.cache.enums.CacheOpTypeEnum;
import com.fxz.fuled.common.cache.objects.CacheIn;
import com.fxz.fuled.common.cache.objects.CacheOut;
import com.fxz.fuled.common.cache.objects.CacheValue;
import com.fxz.fuled.common.chain.Filter;
import com.fxz.fuled.common.chain.Invoker;
import com.fxz.fuled.common.chain.PropertiesFilter;

import java.util.Objects;

public abstract class AbsCacheFilter extends PropertiesFilter implements Filter<CacheIn, CacheOut> {

    private static final String NAME = "AbsCacheFilter";
    private CacheContainer cacheContainer;

    public AbsCacheFilter(CacheContainer cacheContainer) {
        this.cacheContainer = cacheContainer;
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
        return Constant.CACHE_GROUP_NAME;
    }

    @Override
    public boolean enabled() {
        return Boolean.TRUE;
    }

    @Override
    public CacheOut filter(CacheIn cacheIn, Invoker<CacheIn, CacheOut> invoker) {
        //add
        CacheOut invoke;
        try {
            for (CacheIn.SingleOp singleOp : cacheIn.getOpList()) {
                if (CacheOpTypeEnum.SAVE.equals(singleOp.getOpTypeEnum())) {
                    CacheValue cacheValue = cacheContainer.get(singleOp.getKey());
                    if (Objects.nonNull(cacheValue)) {
                        CacheOut cacheOut = new CacheOut();
                        cacheOut.setObject(cacheValue.getObject());
                        cacheOut.setLastAccessTime(cacheValue.getLastAccessTime());
                        cacheOut.setExprInSeconds(cacheValue.getExprInSeconds());
                        return cacheOut;
                    }
                }
            }
            //最终的代理方法
            invoke = invoker.invoke(cacheIn);
            for (CacheIn.SingleOp singleOp : cacheIn.getOpList()) {
                if (CacheOpTypeEnum.UPDATE.equals(singleOp.getOpTypeEnum()) || CacheOpTypeEnum.SAVE.equals(singleOp.getOpTypeEnum())) {
                    if (singleOp.isIncludeNull() || Objects.nonNull(invoke.getObject())) {
                        CacheValue cacheValue = new CacheValue();
                        cacheValue.setObject(invoke.getObject());
                        cacheValue.setLastAccessTime(System.currentTimeMillis());
                        cacheValue.setExprInSeconds(singleOp.getUnit().toSeconds(singleOp.getExpr()));
                        cacheContainer.set(singleOp.getKey(), cacheValue);
                    }
                }
            }
            return invoke;
        } finally {
            for (CacheIn.SingleOp singleOp : cacheIn.getOpList()) {
                if (CacheOpTypeEnum.DELETE.equals(singleOp.getOpTypeEnum())) {
                    cacheContainer.del(singleOp.getKey());
                }
                if (singleOp.isClearLocal()) {
                    cacheContainer.clear();
                }
            }
        }
    }
}
