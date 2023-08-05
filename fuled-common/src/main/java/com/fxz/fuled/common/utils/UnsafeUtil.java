package com.fxz.fuled.common.utils;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * get Unsafe
 */
@Slf4j
public class UnsafeUtil {
    static sun.misc.Unsafe UNSAFE = null;

    static {
        try {
            UNSAFE = getUnsafeInstance();
        } catch (Exception e) {
            log.warn("get unSafe error->{}", e);
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    private static sun.misc.Unsafe getUnsafeInstance()
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field theUnsafeInstance = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (sun.misc.Unsafe) theUnsafeInstance.get(sun.misc.Unsafe.class);
    }
}
