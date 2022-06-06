package com.fxz.fuled.threadpool.monitor;

import java.util.HashMap;
import java.util.Map;

public class RpcContext {
    static ThreadLocal<Map<Object, Object>> store = ThreadLocal.withInitial(() -> new HashMap<>());

    public static Map<Object, Object> get() {
        return store.get();
    }

    public static void set(Map<Object, Object> objectObjectMap) {
        store.set(objectObjectMap);
    }

    public static void remove() {
        store.remove();
    }


}
