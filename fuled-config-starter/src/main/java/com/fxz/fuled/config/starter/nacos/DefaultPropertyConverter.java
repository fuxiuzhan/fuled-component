package com.fxz.fuled.config.starter.nacos;

/**
 * @author fxz
 */
public class DefaultPropertyConverter implements PropertyConverter {
    @Override
    public String processValue(String value) {
        //通过 appId作为密码解密ENC()格式的值
        return value;
    }
}
