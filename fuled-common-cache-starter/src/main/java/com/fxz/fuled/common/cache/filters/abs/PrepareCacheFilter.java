package com.fxz.fuled.common.cache.filters.abs;

import com.fxz.fuled.common.cache.annotation.BatchCache;
import com.fxz.fuled.common.cache.annotation.Cache;
import com.fxz.fuled.common.cache.config.Constant;
import com.fxz.fuled.common.cache.expr.Evaluate;
import com.fxz.fuled.common.cache.objects.CacheIn;
import com.fxz.fuled.common.cache.objects.CacheOut;
import com.fxz.fuled.common.cache.resolver.KeyResolver;
import com.fxz.fuled.common.chain.Invoker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * first
 */
@Slf4j
public class PrepareCacheFilter extends AbsCacheFilter {

    private static final String NAME = "PrepareCacheFilter";

    private KeyResolver keyResolver;

    public PrepareCacheFilter(KeyResolver keyResolver) {
        super(null);
        this.keyResolver = keyResolver;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String filterGroup() {
        return Constant.CACHE_GROUP_NAME;
    }

    @Override
    public CacheOut filter(CacheIn cacheIn, Invoker<CacheIn, CacheOut> invoker) {
        List<CacheIn.SingleOp> opList = new ArrayList<>();
        for (Cache cache : cacheIn.getCaches()) {
            if (evaluateCondition(cacheIn.getProceedingJoinPoint(), cache)) {
                CacheIn.SingleOp singleOp = new CacheIn.SingleOp();
                singleOp.setOpTypeEnum(cache.opType());
                singleOp.setIncludeNull(cache.includeNullResult());
                singleOp.setKey(keyResolver.resolve(cacheIn.getProceedingJoinPoint(), cache));
                singleOp.setExpr(cache.expr());
                singleOp.setUnit(cache.unit());
                opList.add(singleOp);
            }
        }
        cacheIn.setOpList(opList);
        return invoker.invoke(cacheIn);
    }

    private boolean evaluateCondition(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        boolean result = Boolean.TRUE;
        if (Objects.nonNull(cache) && StringUtils.hasText(cache.condition())) {
            try {
                result = Evaluate.evaluate(proceedingJoinPoint, cache.condition(), Boolean.class);
                if (Objects.nonNull(result)) {
                    return result;
                }
            } catch (Exception e) {
                result = Boolean.FALSE;
                log.warn("condition evaluate error，method->{}, error->{}", proceedingJoinPoint.getSignature().getName(), e);
            }
        }
        return result;
    }
}
