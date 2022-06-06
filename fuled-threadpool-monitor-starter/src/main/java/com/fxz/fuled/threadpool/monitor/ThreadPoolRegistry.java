package com.fxz.fuled.threadpool.monitor;

import com.fxz.fuled.threadpool.monitor.manage.Manageable;
import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;
import com.fxz.fuled.threadpool.monitor.reporter.Reporter;
import com.fxz.fuled.threadpool.monitor.wrapper.ScheduledThreadPoolExecutorWrapper;
import com.fxz.fuled.threadpool.monitor.wrapper.ThreadPoolExecutorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 线程池管理工具
 *
 * @author fxz
 */
@Slf4j
public class ThreadPoolRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    /**
     * 上报间隔
     */
    private static int reportInternalInSeconds = 30;
    /**
     * 收集间隔
     */

    private static int clollectInternalInSeconds = 10;
    /**
     * 本地队列大小
     */
    private static int queueSize = 1024;

    /**
     * 单词批量上传
     */
    private static int batchSize = 50;
    /**
     * 队列类型
     */
    private static Queue<ReporterDto> reportQueue;

    private static List<ReporterDto> tempList = null;

    private static AtomicBoolean whileCondition = new AtomicBoolean(Boolean.TRUE);

    /**
     * 先处理ThreadPoolExecutor 以后处理TaskExecutor
     */
    private static Map<String, Manageable> manageableMap = new ConcurrentHashMap();

    /**
     * 状态标志
     */
    private static AtomicBoolean started = new AtomicBoolean(Boolean.FALSE);

    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Math.max(Runtime.getRuntime().availableProcessors(), 4));

    public static void registerThreadPool(String threadPoolName, ThreadPoolExecutor threadPoolExecutor) {
        if (StringUtils.isEmpty(threadPoolName) || Objects.isNull(threadPoolExecutor)) {
            log.error("threadPoolName and threadPool must not be null");
        }
        Manageable manageable = null;
        if (threadPoolExecutor instanceof ScheduledThreadPoolExecutor) {
            manageable = new ScheduledThreadPoolExecutorWrapper(threadPoolName, (ScheduledThreadPoolExecutor) threadPoolExecutor);
        } else {
            manageable = new ThreadPoolExecutorWrapper(threadPoolName, threadPoolExecutor);
        }
        manageableMap.put(threadPoolName, manageable);
        start();
        log.info("threadPoolName->{} registered", threadPoolName);
    }

    private static void start() {
        if (started.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            //starting
            reportQueue = new ArrayBlockingQueue<>(queueSize);
            tempList = new ArrayList<>(batchSize);
            scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> doCollect(), 10, clollectInternalInSeconds, TimeUnit.SECONDS);
            scheduledThreadPoolExecutor.execute(() -> doReport());
        }
    }

    /**
     * 执行上报，可能多个实现
     */
    private static void doReport() {
        while (whileCondition.get()) {
            if (Objects.nonNull(applicationContext)) {
                Map<String, Reporter> beansOfType = applicationContext.getBeansOfType(Reporter.class);
                if (!CollectionUtils.isEmpty(beansOfType)) {
                    try {
                        if (Objects.isNull(tempList)) {
                            tempList = new ArrayList<>(batchSize);
                        }
                        while (tempList.size() < batchSize) {
                            tempList.add(reportQueue.poll());
                        }
                        beansOfType.forEach((k, v) -> {
                            try {
                                v.report(tempList);
                            } catch (Exception e) {
                                log.error("report error ,name->{},error->{}", k, e);
                            }
                        });
                        tempList.clear();
                    } catch (Exception e) {
                        log.error("report error->{}", e);
                    }
                }
            }
        }
    }

    /**
     * fixRate 必须不能抛异常
     */
    private static void doCollect() {
        try {
            if (!CollectionUtils.isEmpty(manageableMap)) {
                manageableMap.forEach((k, v) -> {
                    //不关心结果
                    reportQueue.offer(v.getRecord());
                });
            }
        } catch (Exception e) {
            log.error("collect error->{}", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThreadPoolRegistry.applicationContext = applicationContext;
    }
}
