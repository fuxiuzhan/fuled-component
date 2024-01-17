package com.fxz.fuled.common.cache.container;


import com.fxz.fuled.common.cache.objects.CacheValue;

import java.util.concurrent.TimeUnit;

public interface CacheContainer {

    CacheValue get(String key);

    CacheValue set(String key, CacheValue cacheValue);

    void del(String key);

    void clear();
}
