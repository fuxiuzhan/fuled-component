package com.fxz.fuled.common.cache.objects;

import com.fxz.fuled.common.cache.annotation.BatchCache;
import com.fxz.fuled.common.cache.annotation.Cache;
import com.fxz.fuled.common.cache.enums.CacheOpTypeEnum;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */

@Data
public class CacheIn implements Serializable {
    private List<Cache> caches;
    private ProceedingJoinPoint proceedingJoinPoint;
    private List<SingleOp> opList;
    private Map<String, Object> additions = new HashMap<>();

    @Data
    public static class SingleOp {
        private CacheOpTypeEnum opTypeEnum;
        private String key;
        private boolean includeNull = Boolean.FALSE;
        private int expr = 10;
        private TimeUnit unit = TimeUnit.MINUTES;
        private boolean clearLocal=Boolean.FALSE;

    }
}
