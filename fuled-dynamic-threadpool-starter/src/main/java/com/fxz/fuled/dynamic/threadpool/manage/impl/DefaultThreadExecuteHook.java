package com.fxz.fuled.dynamic.threadpool.manage.impl;

import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;
import com.fxz.fuled.dynamic.threadpool.wrapper.RunnableWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author fuled
 */
public class DefaultThreadExecuteHook implements ThreadExecuteHook {

    @Getter
    @Setter
    private ThreadExecuteHook threadExecuteHook;

    @Override
    public void afterExecute(RunnableWrapper runnableWrapper) {
        if (Objects.nonNull(threadExecuteHook)) {
            threadExecuteHook.afterExecute(runnableWrapper);
        }
        ThreadExecuteHook.super.afterExecute(runnableWrapper);
    }
}
