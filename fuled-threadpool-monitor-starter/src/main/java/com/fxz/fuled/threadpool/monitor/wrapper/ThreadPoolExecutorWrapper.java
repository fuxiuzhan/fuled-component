package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.manage.Manageable;
import com.fxz.fuled.threadpool.monitor.pojo.ChangePair;
import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fxz
 */
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
        return null;
    }

    @Override
    public void onChange(String threadPoolName, List<ChangePair> types) {

    }
}
