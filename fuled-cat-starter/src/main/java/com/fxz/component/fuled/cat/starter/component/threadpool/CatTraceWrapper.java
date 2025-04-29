package com.fxz.component.fuled.cat.starter.component.threadpool;

import java.util.concurrent.Callable;

public class CatTraceWrapper {

    /**
     * @param callable
     * @return
     */
    public static Callable buildCallable(Callable callable, String threadPoolName) {
        return new CallableTracedWrapper(callable, threadPoolName);
    }

    /**
     * @param runnable
     * @return
     */
    public static Runnable buildRunnable(Runnable runnable, String threadPoolName) {
        return new RunnableTracedWrapper(runnable, threadPoolName);
    }
}

