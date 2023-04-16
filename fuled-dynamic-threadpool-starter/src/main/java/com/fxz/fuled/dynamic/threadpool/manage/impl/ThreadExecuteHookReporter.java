package com.fxz.fuled.dynamic.threadpool.manage.impl;

import com.fxz.fuled.common.dynamic.threadpool.reporter.FastStatReporter;
import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;
import com.fxz.fuled.dynamic.threadpool.wrapper.TaskWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;


/**
 * @author fuled
 */
public class ThreadExecuteHookReporter implements ThreadExecuteHook {
    @Autowired(required = false)
    private FastStatReporter fastStatReporter;

    @Override
    public void afterExecute(TaskWrapper taskWrapper) {
        if (Objects.nonNull(fastStatReporter) && !taskWrapper.isWorker()) {
            fastStatReporter.updateStat(taskWrapper.getThreadPoolName()
                    , taskWrapper.queuedDuration()
                    , taskWrapper.executedDuration());
        }
    }
}
