package com.fxz.fuled.dynamic.redis.config;

import com.fxz.fuled.dynamic.redis.properties.DynamicProperties;

/**
 * 转换配置，一般配置是从配置中心回去，也有可能配置是从数据库
 */
public interface ConfigConvert {

    default DynamicProperties convert(DynamicProperties dynamicProperties) {
        return dynamicProperties;
    }
}
