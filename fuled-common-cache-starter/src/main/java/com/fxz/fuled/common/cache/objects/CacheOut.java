package com.fxz.fuled.common.cache.objects;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Data
public class CacheOut {
    private Object object;
    private long lastAccessTime;
    private long exprInSeconds;
    private boolean hasError = Boolean.FALSE;
    private Throwable throwable;
}
