package com.fxz.fuled.common.cache.aspect;

import com.fxz.fuled.common.cache.annotation.BatchCache;
import com.fxz.fuled.common.cache.annotation.Cache;
import com.fxz.fuled.common.cache.config.Constant;
import com.fxz.fuled.common.cache.objects.CacheIn;
import com.fxz.fuled.common.cache.objects.CacheOut;
import com.fxz.fuled.common.chain.FilterChainManger;
import com.fxz.fuled.common.chain.Invoker;
import com.fxz.fuled.common.version.ComponentVersion;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class CacheAspect {

    @Value("${method.cache.enabled:true}")
    private boolean cacheEnabled;

    @Bean("simpleCacheVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-common-cache.version", "1.0.0.waterdrop", "fuled-simple-cache-component");
    }

    @Autowired
    private FilterChainManger filterChainManger;

    private Invoker<CacheIn, CacheOut> invoker;

    @PostConstruct
    public void init() {
        invoker = filterChainManger.getInvoker(Constant.CACHE_GROUP_NAME, o -> {
            throw new RuntimeException("The last filter must be ProceedCacheFilter!");
        });
    }


    @Around("@annotation(com.fxz.fuled.common.cache.annotation.BatchCache) || @annotation(com.fxz.fuled.common.cache.annotation.Cache)")
    public Object processCache(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (cacheEnabled) {
            List<Cache> cacheList = new ArrayList<>();
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Cache cache = methodSignature.getMethod().getAnnotation(Cache.class);
            BatchCache batchCache = methodSignature.getMethod().getAnnotation(BatchCache.class);
            if (Objects.nonNull(cache)) {
                cacheList.add(cache);
            }
            if (Objects.nonNull(batchCache) && !CollectionUtils.isEmpty(Arrays.asList(batchCache.caches()))) {
                cacheList.addAll(Arrays.asList(batchCache.caches()));
            }
            CacheIn cacheIn = new CacheIn();
            cacheIn.setProceedingJoinPoint(proceedingJoinPoint);
            cacheIn.setCaches(cacheList);
            CacheOut invoke = invoker.invoke(cacheIn);
            if (invoke.isHasError()) {
                throw invoke.getThrowable();
            }
            return invoke.getObject();
        }
        return proceedingJoinPoint.proceed();
    }
}
