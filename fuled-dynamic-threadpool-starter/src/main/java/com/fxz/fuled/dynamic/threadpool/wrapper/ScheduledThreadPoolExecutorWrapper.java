package com.fxz.fuled.dynamic.threadpool.wrapper;

import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import com.fxz.fuled.dynamic.threadpool.manage.Manageable;
import lombok.extern.slf4j.Slf4j;

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
    public void updateCoreSize(int coreSize) {
        int old = scheduledThreadPoolExecutor.getCorePoolSize();
        if (old != coreSize) {
            int max = scheduledThreadPoolExecutor.getMaximumPoolSize();
            if (max > coreSize) {
                scheduledThreadPoolExecutor.setCorePoolSize(coreSize);
                scheduledThreadPoolExecutor.setMaximumPoolSize(coreSize);
            } else {
                scheduledThreadPoolExecutor.setMaximumPoolSize(coreSize);
                scheduledThreadPoolExecutor.setCorePoolSize(coreSize);
            }
        }
        log.info("update threadPool name->{} oldCoreSize->{},currentCoreSize->{}", threadPoolName, old, coreSize);
    }

    @Override
    public void shutdown() {
        scheduledThreadPoolExecutor.shutdown();
    }
}
