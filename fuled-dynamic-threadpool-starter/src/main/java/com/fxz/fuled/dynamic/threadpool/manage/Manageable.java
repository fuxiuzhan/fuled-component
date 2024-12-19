package com.fxz.fuled.dynamic.threadpool.manage;

import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import com.fxz.fuled.common.utils.ConfigUtil;
import com.fxz.fuled.common.utils.IPUtil;
import com.fxz.fuled.dynamic.threadpool.ThreadPoolRegistry;
import com.fxz.fuled.dynamic.threadpool.wrapper.RejectHandlerWrapper;
import com.fxz.fuled.dynamic.threadpool.wrapper.ThreadFactoryWrapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fxz
 */
@Slf4j
public abstract class Manageable implements Recordable {

    public abstract void updateCoreSize(int coreSize);

    public BlockingQueue getNative(ThreadPoolExecutor threadPoolExecutor) {
        if (threadPoolExecutor.getQueue() instanceof Proxy) {
            try {
                return (BlockingQueue) getJdkProxyObject(threadPoolExecutor.getQueue());
            } catch (Exception e) {
                log.error("get native queue error->{}", e);
            }
        }
        return threadPoolExecutor.getQueue();
    }

    private Object getJdkProxyObject(Object proxy) throws Exception {
        Method aNative = proxy.getClass().getMethod("getNative", null);
        return aNative.invoke(proxy, null);
    }

    public ReporterDto build(String poolName, ThreadPoolExecutor threadPoolExecutor) {
        ReporterDto reporterDto = new ReporterDto();
        reporterDto.setAppName(ConfigUtil.getAppId());
        reporterDto.setIps(IPUtil.getIpAddress());
        reporterDto.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        reporterDto.setActiveCount(threadPoolExecutor.getActiveCount());
        reporterDto.setTaskCount(threadPoolExecutor.getTaskCount());
        reporterDto.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        reporterDto.setCurrentPoolSize(threadPoolExecutor.getPoolSize());
        reporterDto.setThreadPoolName(poolName);
        reporterDto.setExecCount(threadPoolExecutor.getCompletedTaskCount());
        reporterDto.setQueueType(getNative(threadPoolExecutor).getClass().getName());
        reporterDto.setCurrentQueueSize(threadPoolExecutor.getQueue().size());
        reporterDto.setQueueMaxSize(threadPoolExecutor.getQueue().remainingCapacity() + threadPoolExecutor.getQueue().size());
        reporterDto.setRejectCount(0L);
        reporterDto.setVersion(ThreadPoolRegistry.class.getPackage().getImplementationVersion());
        reporterDto.setLargestPoolSize(threadPoolExecutor.getLargestPoolSize());
        if (threadPoolExecutor.getRejectedExecutionHandler() instanceof RejectHandlerWrapper) {
            reporterDto.setRejectCount(((RejectHandlerWrapper) threadPoolExecutor.getRejectedExecutionHandler()).getCounter());
        }
        reporterDto.setWorkerCreateCount(0);
        if (threadPoolExecutor.getThreadFactory() instanceof ThreadFactoryWrapper) {
            reporterDto.setWorkerCreateCount(((ThreadFactoryWrapper) threadPoolExecutor.getThreadFactory()).getCounter());
        }
        reporterDto.setThreadPoolType(threadPoolExecutor.getClass().getName());
        reporterDto.setRejectHandlerType(threadPoolExecutor.getRejectedExecutionHandler().getClass().getName());
        if (threadPoolExecutor.getRejectedExecutionHandler() instanceof RejectHandlerWrapper) {
            reporterDto.setRejectHandlerType(((RejectHandlerWrapper) threadPoolExecutor.getRejectedExecutionHandler()).getRejectedExecutionHandler().getClass().getName());
        }
        return reporterDto;
    }

    /**
     * shutdown
     */
    public abstract void shutdown();
}
