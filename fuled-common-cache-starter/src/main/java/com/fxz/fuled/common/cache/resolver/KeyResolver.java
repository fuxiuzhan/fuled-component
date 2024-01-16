package com.fxz.fuled.common.cache.resolver;

import com.fxz.fuled.common.cache.annotation.Cache;
import org.aspectj.lang.ProceedingJoinPoint;

public interface KeyResolver {

    String resolve(ProceedingJoinPoint proceedingJoinPoint, Cache cache);
}
