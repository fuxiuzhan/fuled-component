package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;

import java.util.Map;

/**
 * @author fxz
 */
public class RunnableWrapper implements Runnable {
    private Map<Object, Object> metaMap;
    private Runnable runnable;

    public RunnableWrapper(Runnable runnable, Map<Object, Object> metaMap) {
        this.metaMap = metaMap;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            RpcContext.set(metaMap);
            runnable.run();
        } finally {
            RpcContext.remove();
        }
    }
}
