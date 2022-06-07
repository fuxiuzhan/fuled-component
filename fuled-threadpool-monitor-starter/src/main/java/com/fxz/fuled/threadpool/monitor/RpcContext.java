package com.fxz.fuled.threadpool.monitor;

import com.fxz.fuled.threadpool.monitor.manage.ObjectClone;

public class RpcContext {

    public static void setObjectCone(ObjectClone objectClone) {
        RpcContext.objectClone = objectClone;
    }

    /**
     * 复杂对象需要做深copy
     */
    private static ObjectClone objectClone = obj -> obj;

    private static ThreadLocal<Object> store = new ThreadLocal<>();

    public static Object get() {
        return objectClone.clone(store.get());
    }

    public static void set(Object obj) {
        store.set(obj);
    }

    public static void remove() {
        store.remove();
    }


}
