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
            //此处增加方法即可实现如下两个只有继承线程池才能实现的方法
            //beforeExecute
            runnable.run();
        } finally {
            RpcContext.remove();
            //afterExecute
        }
    }
}
