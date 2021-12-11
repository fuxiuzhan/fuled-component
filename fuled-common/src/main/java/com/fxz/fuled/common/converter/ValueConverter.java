package com.fxz.fuled.common.converter;

/**
 * @author fxz
 */
public interface ValueConverter {

    /**
     * convert value
     * encrypt or other process
     *
     * @param value
     * @return
     */
    String convert(String value);
}
