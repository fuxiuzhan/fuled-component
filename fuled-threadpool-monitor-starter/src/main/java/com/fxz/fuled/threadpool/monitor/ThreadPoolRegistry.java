package com.fxz.fuled.threadpool.monitor;

import com.fxz.fuled.threadpool.monitor.manage.Manageable;
import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;
import com.fxz.fuled.threadpool.monitor.pojo.ThreadPoolProperties;
import com.fxz.fuled.threadpool.monitor.reporter.Reporter;
import com.fxz.fuled.threadpool.monitor.wrapper.QueueWrapper;
import com.fxz.fuled.threadpool.monitor.wrapper.ScheduledThreadPoolExecutorWrapper;
import com.fxz.fuled.threadpool.monitor.wrapper.ThreadFactoryWrapper;
import com.fxz.fuled.threadpool.monitor.wrapper.ThreadPoolExecutorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
    private static BlockingQueue<ReporterDto> reportQueue;

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

    /**
     * 线程池注册入口
     *
     * @param threadPoolName
     * @param threadPoolExecutor
     */
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
        //想办法代理queue

        BlockingQueue wrapperQueue = QueueWrapper.wrapper(threadPoolExecutor.getQueue());
        try {
            modifyFinal(threadPoolExecutor, "workQueue", wrapperQueue);
        } catch (Exception e) {
            log.error("inject queue error ->{}", e);
        }
        threadPoolExecutor.setThreadFactory(new ThreadFactoryWrapper(threadPoolExecutor.getThreadFactory()));
        manageableMap.put(threadPoolName, manageable);
        start();
        log.info("threadPoolName->{} registered", threadPoolName);
    }

    /**
     * 更新线程池核心线程数
     *
     * @param threadPoolName
     * @param coreSize
     */
    public static void updateCoreSize(String threadPoolName, int coreSize) {
        if (coreSize <= 0) {
            log.error("coreSize must be >0,name->{},size->{}", threadPoolName, coreSize);
            return;
        }
        Manageable manageable = manageableMap.get(threadPoolName);
        if (Objects.isNull(manageable)) {
            log.error("threadPoolName not exits");
            return;
        }
        manageable.updateCoreSize(coreSize);
    }

    private static void modifyFinal(Object object, String fieldName, Object newFieldValue) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        field.set(object, newFieldValue);
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
                        long start = System.currentTimeMillis();
                        while ((tempList.size() < batchSize) && ((System.currentTimeMillis() - start) < reportInternalInSeconds * 1000L)) {
                            ReporterDto poll = reportQueue.poll(1000, TimeUnit.MILLISECONDS);
                            if (Objects.nonNull(poll)) {
                                tempList.add(poll);
                            }
                        }
                        if (!CollectionUtils.isEmpty(tempList)) {
                            beansOfType.forEach((k, v) -> {
                                try {
                                    v.report(tempList);
                                } catch (Exception e) {
                                    log.error("report error ,name->{},error->{}", k, e);
                                }
                            });
                        }
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
                    ReporterDto record = v.getRecord();
                    if (Objects.nonNull(record)) {
                        reportQueue.offer(record);
                    }
                });
            }
        } catch (Exception e) {
            log.error("collect error->{}", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThreadPoolRegistry.applicationContext = applicationContext;
        wrapperContext();
        eventListener(null);
    }

    /**
     * 包装容器内的threadPool
     */
    private void wrapperContext() {
        ThreadPoolProperties bean = applicationContext.getBean(ThreadPoolProperties.class);
        if (Objects.nonNull(bean)) {
            if (bean.isWrapperContext()) {
                Map<String, ThreadPoolExecutor> threadPools = applicationContext.getBeansOfType(ThreadPoolExecutor.class);
                if (!CollectionUtils.isEmpty(threadPools)) {
                    threadPools.forEach((k, v) -> {
                        registerThreadPool(k, v);
                    });
                }
                Map<String, ScheduledThreadPoolExecutor> scheduledPools = applicationContext.getBeansOfType(ScheduledThreadPoolExecutor.class);
                if (!CollectionUtils.isEmpty(scheduledPools)) {
                    scheduledPools.forEach((k, v) -> {
                        registerThreadPool(k, v);
                    });
                }
            }
        }
    }

    /**
     * 动态变更threadPool参数
     *
     * @param event
     */
    @EventListener
    public void eventListener(EnvironmentChangeEvent event) {
        ThreadPoolProperties bean = applicationContext.getBean(ThreadPoolProperties.class);
        if (Objects.nonNull(bean)) {
            if (!CollectionUtils.isEmpty(bean.getConfig())) {
                bean.getConfig().forEach((k, v) -> {
                    updateCoreSize(k, v.getCoreSize());
                });
            }
        }
    }
}
