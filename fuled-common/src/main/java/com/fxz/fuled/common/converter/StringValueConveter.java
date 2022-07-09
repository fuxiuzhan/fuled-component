package com.fxz.fuled.common.converter;

public interface StringValueConveter {

    /**
     * 加密
     *
     * @param value
     * @return
     */
    default String encrypt(String value) throws Exception {
        return value;
    }

    /**
     * 解密
     *
     * @param value
     * @return
     */
    default String decrypt(String value) throws Exception {
        return value;
    }
}
