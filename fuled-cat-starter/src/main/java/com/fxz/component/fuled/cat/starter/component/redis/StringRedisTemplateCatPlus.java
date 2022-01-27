package com.fxz.component.fuled.cat.starter.component.redis;

import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;

/**
 * @author fxz
 */
public class StringRedisTemplateCatPlus extends StringRedisTemplate {
    public StringRedisTemplateCatPlus() {
    }

    public StringRedisTemplateCatPlus(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    @Override
    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return new DefaultStringRedisConnection(connection);
    }

    @Override
    public HashOperations<String, String, String> opsForHash() {
        return (HashOperations) (new CatCglibProxy()).getInstance(super.opsForHash(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public ValueOperations<String, String> opsForValue() {
        return (ValueOperations) (new CatCglibProxy()).getInstance(super.opsForValue(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public ListOperations<String, String> opsForList() {
        return (ListOperations) (new CatCglibProxy()).getInstance(super.opsForList(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public SetOperations<String, String> opsForSet() {
        return (SetOperations) (new CatCglibProxy()).getInstance(super.opsForSet(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public ZSetOperations<String, String> opsForZSet() {
        return (ZSetOperations) (new CatCglibProxy()).getInstance(super.opsForZSet(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public GeoOperations<String, String> opsForGeo() {
        return (GeoOperations) (new CatCglibProxy()).getInstance(super.opsForGeo(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }

    @Override
    public HyperLogLogOperations<String, String> opsForHyperLogLog() {
        return (HyperLogLogOperations) (new CatCglibProxy()).getInstance(super.opsForHyperLogLog(), new Class[]{RedisTemplate.class}, new Object[]{this});
    }
}
