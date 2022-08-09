package com.fxz.fuled.simple.cache;


import com.fxz.fuled.common.utils.ConfigUtil;
import com.fxz.fuled.common.version.ComponentVersion;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author fxz
 */
@Slf4j
@Aspect
@Component
public class CacheAspect {

    @Value("${method.cache.enabled:true}")
    private boolean cacheEnabled;

    @Bean("simpleCacheVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-simple-cache.version", "1.0.0.waterdrop", "fuled-simple-cache-component");
    }

    private static final String METHOD_CACHE_PREFIX = ConfigUtil.getAppId();
    /**
     * 使用StringRedisTemplate 需要实现自己的typeConverter
     * 使用RedisTemplate则不需要，使用RedisTemplate提供的converter
     * <p>
     * 还是妥协一下，使用Redis自带的序列化工具，
     * 自己实现的话需要处理太多东西，相当于Redis序列化中的各种类型的Convert
     * 都要重写一遍，没必要了。
     */
    @Autowired(required = false)
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;
    LruCache lruCache = new LruCache(4096);

    @Around("@annotation(com.fxz.fuled.simple.cache.BatchCache) || @annotation(com.fxz.fuled.simple.cache.Cache)")
    public Object processCache(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (cacheEnabled) {
            OpTypeList opTypeList = assembleOpTypeList(proceedingJoinPoint);
            try {
                CacheValue result = getCache(proceedingJoinPoint, opTypeList.getSaveList());
                if (Objects.nonNull(result)) {
                    return result.getObject();
                }
                Object proceedResult = proceedingJoinPoint.proceed();
                setCache(proceedingJoinPoint, opTypeList.getSaveList(), proceedResult);
                return proceedResult;
            } finally {
                delCache(proceedingJoinPoint, opTypeList.getDelList());
            }
        }
        return proceedingJoinPoint.proceed();
    }


    /**
     * 组装处理类型的list
     *
     * @param proceedingJoinPoint
     * @return
     */
    private OpTypeList assembleOpTypeList(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        List<Cache> saveList = new ArrayList<>();
        List<Cache> delList = new ArrayList<>();
        List<Cache> rawList = new ArrayList<>();
        Cache cache = methodSignature.getMethod().getAnnotation(Cache.class);
        BatchCache batchCache = methodSignature.getMethod().getAnnotation(BatchCache.class);
        if (Objects.nonNull(cache)) {
            rawList.add(cache);
        }
        if (Objects.nonNull(batchCache)) {
            if (batchCache.caches().length > 0) {
                for (Cache c : batchCache.caches()) {
                    rawList.add(c);
                }
            }
        }
        rawList.forEach(r -> {
            if (r.opType().equals(CacheOpTypeEnum.SAVE)) {
                saveList.add(r);
            } else {
                delList.add(r);
            }
        });
        OpTypeList opTypeList = new OpTypeList();
        opTypeList.setDelList(delList);
        opTypeList.setSaveList(saveList);
        opTypeList.setRawList(rawList);
        return opTypeList;
    }

    private CacheValue getCache(ProceedingJoinPoint proceedingJoinPoint, List<Cache> cacheList) {
        if (Objects.nonNull(cacheList) && cacheList.size() > 0) {
            for (Cache cache : cacheList) {
                if (cache.clearLocal()) {
                    lruCache.clear();
                    return null;
                }
                if (evaluateCondition(proceedingJoinPoint, cache)) {
                    String key = evaluateKey(proceedingJoinPoint, cache);
                    if (cache.localTurbo() || cache.localCacheOnly()) {
                        Object o = lruCache.get(key);
                        if (o != null && o instanceof CacheValue) {
                            CacheValue localCacheValue = (CacheValue) o;
                            if (localCacheValue.getExprInSeconds() > 0) {
                                if ((System.currentTimeMillis() - localCacheValue.getLastAccessTime()) > localCacheValue.getExprInSeconds() * 1000L) {
                                    lruCache.remove(key);
                                } else {
                                    return localCacheValue;
                                }
                            } else {
                                return localCacheValue;
                            }
                        }
                    }
                    if (Objects.nonNull(redisTemplate) && !cache.localCacheOnly()) {
                        Object o = redisTemplate.opsForValue().get(key);
                        if (Objects.nonNull(o) && o instanceof CacheValue) {
                            return (CacheValue) o;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void setCache(ProceedingJoinPoint proceedingJoinPoint, List<Cache> cacheList, Object result) {
        if (Objects.nonNull(cacheList) && cacheList.size() > 0) {
            for (Cache cache : cacheList) {
                if (!cache.clearLocal() && evaluateCondition(proceedingJoinPoint, cache)) {
                    String key = evaluateKey(proceedingJoinPoint, cache);
                    CacheValue cacheValue = new CacheValue();
                    cacheValue.setLastAccessTime(System.currentTimeMillis());
                    cacheValue.setExprInSeconds(cache.unit().toSeconds(cache.expr()));
                    cacheValue.setObject(result);
                    if (cache.localTurbo() || cache.localCacheOnly()) {
                        if (Objects.nonNull(result) || cache.includeNullResult()) {
                            lruCache.put(key, cacheValue);
                        }
                    }
                    if (Objects.nonNull(redisTemplate) && !cache.localCacheOnly() && (Objects.nonNull(result) || cache.includeNullResult())) {
                        redisTemplate.opsForValue().set(key, cacheValue, cache.expr(), cache.unit());
                    }
                }
            }
        }
    }

    private void delCache(ProceedingJoinPoint proceedingJoinPoint, List<Cache> cacheList) {
        if (Objects.nonNull(cacheList) && cacheList.size() > 0) {
            cacheList.stream().forEach(singleCache -> {
                if (evaluateCondition(proceedingJoinPoint, singleCache)) {
                    String key = evaluateKey(proceedingJoinPoint, singleCache);
                    if (singleCache.localTurbo()) {
                        lruCache.remove(key);
                    }
                    if (Objects.nonNull(redisTemplate)) {
                        redisTemplate.delete(key);
                    }
                }
            });
        }
    }

    private boolean evaluateCondition(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        boolean result = Boolean.TRUE;
        if (Objects.nonNull(cache) && StringUtils.hasText(cache.condition())) {
            try {
                result = evaluate(proceedingJoinPoint, cache.condition(), Boolean.class);
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

    private String evaluateKey(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        if (Objects.nonNull(cache) && StringUtils.hasText(cache.key())) {
            try {
                String keyPrefix = cache.prefix();
                String key = evaluate(proceedingJoinPoint, cache.key(), String.class);
                return METHOD_CACHE_PREFIX + keyPrefix + key;
            } catch (Exception e) {
                log.warn("cache annotation expression error using default key instead，method->{}, error->{}", proceedingJoinPoint.getSignature().getName(), e);
            }
        }
        return defaultKey(proceedingJoinPoint);
    }

    private <T> T evaluate(ProceedingJoinPoint proceedingJoinPoint, String expression, Class clazz) {
        EvaluationContext context = new StandardEvaluationContext();
        ExpressionParser parser = new SpelExpressionParser();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Parameter[] ps = methodSignature.getMethod().getParameters();
        if (ps != null) {
            for (int j = 0, l = ps.length; j < l; ++j) {
                context.setVariable(ps[j].getName(), proceedingJoinPoint.getArgs()[j]);
            }
        }
        return (T) parser.parseExpression(expression).getValue(context, clazz);
    }

    private String defaultKey(ProceedingJoinPoint proceedingJoinPoint) {
        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String value = METHOD_CACHE_PREFIX + "_" + className + "_" + methodSignature.getName() + "_" + methodSignature.getMethod().getReturnType().getSimpleName() + "_" + proceedingJoinPoint.getArgs().length + "_" + Arrays.deepHashCode(proceedingJoinPoint.getArgs());
        return value;
    }
}

@Data
class OpTypeList {
    List<Cache> saveList;
    List<Cache> delList;
    List<Cache> rawList;
}

@Data
class CacheValue implements Serializable {
    private Object object;
    private long lastAccessTime;
    private long exprInSeconds;
}

class LruCache extends LinkedHashMap {
    int size;

    LruCache(int size) {
        super(size);
        this.size = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > size;
    }
}