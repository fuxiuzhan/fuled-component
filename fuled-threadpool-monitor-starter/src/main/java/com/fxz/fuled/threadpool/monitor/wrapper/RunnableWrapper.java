package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;

/**
 * @author fxz
 */
public class RunnableWrapper implements Runnable {
    private Object meta;
    private Runnable runnable;

    public RunnableWrapper(Runnable runnable, Object meta) {
        this.meta = meta;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            RpcContext.set(meta);
            runnable.run();
        } finally {
            RpcContext.remove();
        }
    }
}
