package com.fxz.fuled.dynamic.redis.properties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果有原生的redis
 * 可以将原生的配置提出来作为默认配置
 * 会将master指定的redis 作为默认注入容器
 * <p>
 * spring.dynamic.redis.master=default
 * spring.dynamic.redis.default=xxxxx
 *
 * @author fxz
 */
@ConfigurationProperties("fuled.dynamic.redis")
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
