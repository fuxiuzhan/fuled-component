package com.fxz.fuled.dynamic.threadpool.wrapper;

import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import com.fxz.fuled.dynamic.threadpool.manage.Manageable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fxz
 */
@Slf4j
public class ThreadPoolExecutorWrapper extends Manageable {
    private ThreadPoolExecutor threadPoolExecutor;
    private String threadPoolName;

    private RejectHandlerWrapper rejectHandlerWrapper;

    public ThreadPoolExecutorWrapper(String threadPoolName, ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.threadPoolName = threadPoolName;
        rejectHandlerWrapper = new RejectHandlerWrapper(threadPoolExecutor.getRejectedExecutionHandler());
        threadPoolExecutor.setRejectedExecutionHandler(rejectHandlerWrapper);
    }


    @Override
    public ReporterDto getRecord() {
        return build(threadPoolName, threadPoolExecutor);
    }

    @Override
    public void updateCoreSize(int coreSize) {
        int old = threadPoolExecutor.getCorePoolSize();
        if (old != coreSize) {
            int max = threadPoolExecutor.getMaximumPoolSize();
            if (max > coreSize) {
                threadPoolExecutor.setCorePoolSize(coreSize);
                threadPoolExecutor.setMaximumPoolSize(coreSize);
            } else {
                threadPoolExecutor.setMaximumPoolSize(coreSize);
                threadPoolExecutor.setCorePoolSize(coreSize);
            }
        }
        log.info("update threadPool name->{} oldCoreSize->{},currentCoreSize->{}", threadPoolName, old, coreSize);
    }

    @Override
    public void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
