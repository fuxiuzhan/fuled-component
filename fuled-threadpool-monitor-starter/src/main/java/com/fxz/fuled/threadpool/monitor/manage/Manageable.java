package com.fxz.fuled.threadpool.monitor.manage;

import com.fxz.fuled.common.utils.ConfigUtil;
import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;
import com.fxz.fuled.threadpool.monitor.wrapper.RejectHandlerWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 尝试实现任务提交时对方法代理
 * <p>
 * 包装对象达到传递上下文的目的
 *
 * @author fxz
 */
@Slf4j
public abstract class Manageable implements ChangeListener, Recordable {

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
        reporterDto.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        reporterDto.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        reporterDto.setCurrentPoolSize(threadPoolExecutor.getPoolSize());
        reporterDto.setThreadPoolName(poolName);
        reporterDto.setExecCount(threadPoolExecutor.getCompletedTaskCount());
        reporterDto.setQueueType(getNative(threadPoolExecutor).getClass().getName());
        reporterDto.setCurrentQueueSize(threadPoolExecutor.getQueue().size());
        reporterDto.setRejectCnt(0L);
        if (threadPoolExecutor.getRejectedExecutionHandler() instanceof RejectHandlerWrapper) {
            reporterDto.setRejectCnt(((RejectHandlerWrapper) threadPoolExecutor.getRejectedExecutionHandler()).getCounter());
        }
        reporterDto.setThreadPoolType(threadPoolExecutor.getThreadFactory().getClass().getName());
        reporterDto.setRejectHandlerType(threadPoolExecutor.getRejectedExecutionHandler().getClass().getName());
        if (threadPoolExecutor.getRejectedExecutionHandler() instanceof RejectHandlerWrapper) {
            reporterDto.setRejectHandlerType(((RejectHandlerWrapper) threadPoolExecutor.getRejectedExecutionHandler()).getRejectedExecutionHandler().getClass().getName());
        }
        return reporterDto;
    }
}
