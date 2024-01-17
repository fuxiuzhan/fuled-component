package com.fxz.fuled.common.cache.resolver;

import com.fxz.fuled.common.cache.annotation.Cache;
import com.fxz.fuled.common.cache.expr.Evaluate;
import com.fxz.fuled.common.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class DefaultKeyResolver implements KeyResolver {
    private static final String METHOD_CACHE_PREFIX = ConfigUtil.getAppId();
    @Override
    public String resolve(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        return evaluateKey(proceedingJoinPoint, cache);
    }

    private String evaluateKey(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        if (Objects.nonNull(cache) && StringUtils.hasText(cache.key())) {
            try {
                String keyPrefix = cache.prefix();
                String key = Evaluate.evaluate(proceedingJoinPoint, cache.key(), String.class);
                return METHOD_CACHE_PREFIX + keyPrefix + key;
            } catch (Exception e) {
                log.warn("cache annotation expression error using default key instead，method->{}, error->{}", proceedingJoinPoint.getSignature().getName(), e);
            }
        }
        return defaultKey(proceedingJoinPoint);
    }


    private String defaultKey(ProceedingJoinPoint proceedingJoinPoint) {
        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String value = METHOD_CACHE_PREFIX + "_" + className + "_" + methodSignature.getName() + "_" + methodSignature.getMethod().getReturnType().getSimpleName() + "_" + proceedingJoinPoint.getArgs().length + "_" + Arrays.deepHashCode(proceedingJoinPoint.getArgs());
        return value;
    }
}
