package com.fxz.fuled.common.cache.container;


import com.fxz.fuled.common.cache.objects.CacheValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class LruCacheContainer implements CacheContainer {
    private LruCache lruCache;
    private int defaultSize = 4096;

    public LruCacheContainer() {
        this.lruCache = new LruCache(defaultSize);
    }

    public LruCacheContainer(int maxSize) {
        this.lruCache = new LruCache(maxSize);
    }

    @Override
    public CacheValue get(String key) {
        CacheValue o = lruCache.get(key);
        if (Objects.nonNull(o)) {
            if (System.currentTimeMillis() - o.getLastAccessTime() > o.getExprInSeconds() * 1000) {
                lruCache.remove(key);
            }
            return o;
        }
        return null;
    }

    @Override
    public CacheValue set(String key, CacheValue cacheValue) {
        return lruCache.put(key, cacheValue);
    }

    @Override
    public void del(String key) {
        lruCache.remove(key);
    }

    @Override
    public void clear() {
        lruCache.clear();
    }

    class LruCache extends LinkedHashMap<String, CacheValue> {
        private ReentrantLock reentrantLock = new ReentrantLock();
        int size;

        LruCache(int size) {
            super(size);
            this.size = size;
        }

        @Override
        public CacheValue put(String key, CacheValue value) {
            reentrantLock.lock();
            try {
                return super.put(key, value);
            } finally {
                reentrantLock.unlock();
            }
        }

        @Override
        public CacheValue remove(Object key) {
            reentrantLock.lock();
            try {
                return super.remove(key);
            } finally {
                reentrantLock.unlock();
            }
        }

        @Override
        public void clear() {
            reentrantLock.lock();
            try {
                super.clear();
            } finally {
                reentrantLock.unlock();
            }
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > size;
        }
    }
}
