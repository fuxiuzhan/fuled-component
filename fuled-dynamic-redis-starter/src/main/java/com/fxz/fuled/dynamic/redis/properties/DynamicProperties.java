package com.fxz.fuled.dynamic.redis.properties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fxz
 */
@ConfigurationProperties("spring.dynamic.redis")
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

    public void setMaster(String master) {
        this.master = master;
    }

    public void setConfig(Map<String, RedisProperties> config) {
        this.config = config;
    }

    public String getMaster() {
        return master;
    }

    public Map<String, RedisProperties> getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "DynamicProperties{" +
                "master='" + master + '\'' +
                ", config=" + config +
                '}';
    }
}
