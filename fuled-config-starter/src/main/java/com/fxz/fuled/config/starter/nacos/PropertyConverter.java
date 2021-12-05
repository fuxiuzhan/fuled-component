package com.fxz.fuled.config.starter.nacos;

/**
 * @author fxz
 */
public interface PropertyConverter {

    /**
     * 转换及加解密
     * @param value
     * @return
     */
    String processValue(String value);
}
