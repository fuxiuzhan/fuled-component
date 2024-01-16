package com.fxz.fuled.common.cache.container;

import com.fxz.fuled.common.cache.objects.CacheValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisCacheContainer implements CacheContainer {

    private RedisTemplate redisTemplate;

    public RedisCacheContainer(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CacheValue get(String key) {
        return (CacheValue) redisTemplate.opsForValue().get(key);
    }

    @Override
    public CacheValue set(String key, CacheValue cacheValue) {
        redisTemplate.opsForValue().set(key, cacheValue, cacheValue.getExprInSeconds(), TimeUnit.SECONDS);
        return cacheValue;
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void clear() {
        log.warn("redis clear not support!");
    }
}
