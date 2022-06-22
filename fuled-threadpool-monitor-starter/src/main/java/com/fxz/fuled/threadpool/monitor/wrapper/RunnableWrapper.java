package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;
import com.fxz.fuled.threadpool.monitor.manage.ThreadExecuteHook;

/**
 * @author fxz
 */
public class RunnableWrapper implements Runnable {
    private Object meta;
    private Runnable runnable;

    private ThreadExecuteHook threadExecuteHook;

    public RunnableWrapper(Runnable runnable, Object meta, ThreadExecuteHook threadExecuteHook) {
        this.meta = meta;
        this.runnable = runnable;
        this.threadExecuteHook = threadExecuteHook;
    }

    @Override
    public void run() {
        try {
            //将threadLocal设置在hook可见范围内
            RpcContext.set(meta);
            //此处增加方法即可实现如下两个只有继承线程池才能实现的方法
            //beforeExecute
            threadExecuteHook.beforeExecute(runnable);
            runnable.run();
        } catch (Throwable throwable) {
            threadExecuteHook.onException(runnable, throwable);
        } finally {
            threadExecuteHook.afterExecute(runnable);
            RpcContext.remove();
            //afterExecute
        }
    }
}
