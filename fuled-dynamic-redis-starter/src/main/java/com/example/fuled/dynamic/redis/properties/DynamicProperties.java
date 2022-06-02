package com.example.fuled.dynamic.redis.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fxz
 */
@ConfigurationProperties(prefix = "spring.dynamic.redis")
@Data
public class DynamicProperties {
    /**
     * set primary redis
     * 设置后默认注入指定的链接
     */
    private String master;

    /**
     * 多redis配置
     */
    Map<String, RedisProperties> config = new HashMap();
}
