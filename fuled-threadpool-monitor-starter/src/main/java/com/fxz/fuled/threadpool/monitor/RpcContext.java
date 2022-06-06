package com.fxz.fuled.threadpool.monitor;

import com.fxz.fuled.threadpool.monitor.manage.ObjectCone;

public class RpcContext {

    public static void setObjectCone(ObjectCone objectCone) {
        RpcContext.objectCone = objectCone;
    }

    /**
     * 复杂对象需要做深copy
     */
    private static ObjectCone objectCone = obj -> obj;

    private static ThreadLocal<Object> store = new ThreadLocal<>();

    public static Object get() {
        return objectCone.clone(store.get());
    }

    public static void set(Object obj) {
        store.set(obj);
    }

    public static void remove() {
        store.remove();
    }


}
