package com.fxz.fuled.dynamic.threadpool.wrapper;

import com.fxz.fuled.dynamic.threadpool.manage.Manageable;
import com.fxz.fuled.dynamic.threadpool.pojo.ChangePair;
import com.fxz.fuled.dynamic.threadpool.pojo.ReporterDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author fxz
 */
@Slf4j
public class ScheduledThreadPoolExecutorWrapper extends Manageable {
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private String threadPoolName;

    private RejectHandlerWrapper rejectHandlerWrapper;

    public ScheduledThreadPoolExecutorWrapper(String threadPoolName, ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        this.threadPoolName = threadPoolName;
        rejectHandlerWrapper = new RejectHandlerWrapper(scheduledThreadPoolExecutor.getRejectedExecutionHandler());
        scheduledThreadPoolExecutor.setRejectedExecutionHandler(rejectHandlerWrapper);
    }

    @Override
    public ReporterDto getRecord() {
        return build(threadPoolName, scheduledThreadPoolExecutor);
    }

    @Override
    public void onChange(String threadPoolName, List<ChangePair> types) {

    }

    @Override
    public void updateCoreSize(int coreSize) {
        int old = scheduledThreadPoolExecutor.getCorePoolSize();
        if (old != coreSize) {
            scheduledThreadPoolExecutor.setCorePoolSize(coreSize);
            scheduledThreadPoolExecutor.setMaximumPoolSize(coreSize);
        }
        log.info("update threadPool oldCoreSize->{},currentCoreSize->{}", old, coreSize);
    }

    @Override
    public void shutdown() {
        scheduledThreadPoolExecutor.shutdown();
    }
}
