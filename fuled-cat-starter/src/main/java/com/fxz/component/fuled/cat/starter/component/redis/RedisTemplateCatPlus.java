package com.fxz.component.fuled.cat.starter.component.redis;

import org.springframework.data.redis.core.*;

/**
 * @author fxz
 */
public class RedisTemplateCatPlus<K, V> extends RedisTemplate<K, V> {
    public RedisTemplateCatPlus() {
    }

    @Override
    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return (HashOperations) (new CatCglibProxy()).getInstance(super.opsForHash(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public ValueOperations<K, V> opsForValue() {
        return (ValueOperations) (new CatCglibProxy()).getInstance(super.opsForValue(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public ListOperations<K, V> opsForList() {
        return (ListOperations) (new CatCglibProxy()).getInstance(super.opsForList(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public SetOperations<K, V> opsForSet() {
        return (SetOperations) (new CatCglibProxy()).getInstance(super.opsForSet(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public ZSetOperations<K, V> opsForZSet() {
        return (ZSetOperations) (new CatCglibProxy()).getInstance(super.opsForZSet(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public GeoOperations<K, V> opsForGeo() {
        return (GeoOperations) (new CatCglibProxy()).getInstance(super.opsForGeo(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public HyperLogLogOperations<K, V> opsForHyperLogLog() {
        return (HyperLogLogOperations) (new CatCglibProxy()).getInstance(super.opsForHyperLogLog(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }
}
