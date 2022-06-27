package com.fxz.fuled.threadpool.monitor.manage;

/**
 * threadLocal 传递控制
 */
public interface ThreadLocalTransmitSupport {

    /**
     * threadLocal传递开关
     *
     * @return
     */
    default boolean threadLocalSupport() {
        return Boolean.FALSE;
    }
}
