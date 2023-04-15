package com.fxz.fuled.dynamic.threadpool;

import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import com.fxz.fuled.common.dynamic.threadpool.reporter.Reporter;
import com.fxz.fuled.common.utils.ThreadFactoryNamed;
import com.fxz.fuled.dynamic.threadpool.manage.Manageable;
import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;
import com.fxz.fuled.dynamic.threadpool.manage.impl.DefaultThreadExecuteHook;
import com.fxz.fuled.dynamic.threadpool.pojo.ThreadPoolProperties;
import com.fxz.fuled.dynamic.threadpool.wrapper.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ThreadPoolRegistry implements ApplicationContextAware, ApplicationRunner {

    private static ApplicationContext applicationContext;
    /**
     * 上报间隔
     */
    private static int reportInternalInSeconds = 10;
    /**
     * 收集间隔
     */

    private static int clollectInternalInSeconds = 10;
    /**
     * 本地队列大小
     */
    private static int queueSize = 1024;

    /**
     * 单次批量上传
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

    private static DefaultThreadExecuteHook defaultExecuteHook = new DefaultThreadExecuteHook();

    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(Math.max(Runtime.getRuntime().availableProcessors(), 4), ThreadFactoryNamed.named("thread-monitor", Boolean.TRUE));


    /**
     * 线程池注册入口
     *
     * @param threadPoolName
     * @param threadPoolExecutor
     */
    public static void registerThreadPool(String threadPoolName, ThreadPoolExecutor threadPoolExecutor) {
        registerThreadPool(threadPoolName, threadPoolExecutor, defaultExecuteHook);
    }

    /**
     * 线程池注册入口
     *
     * @param threadPoolName
     * @param threadPoolExecutor
     * @param threadExecuteHook
     */
    public static void registerThreadPool(String threadPoolName, ThreadPoolExecutor threadPoolExecutor, ThreadExecuteHook threadExecuteHook) {
        if (StringUtils.isEmpty(threadPoolName) || Objects.isNull(threadPoolExecutor)) {
            log.error("threadPoolName and threadPool must not be null");
            return;
        }
        if (!manageableMap.containsKey(threadPoolName)) {
            Manageable manageable;
            if (threadPoolExecutor instanceof ScheduledThreadPoolExecutor) {
                //线程池的监控主要是监控ThreadPoolExecutor，对于定时线程池由于其内部队列原理是采用list的大小堆排序的queue
                //所以像核心线程数最大线程数拒绝策略均有所不同，此处增加处理作原理说明，此处不处理定时线程池
                manageable = new ScheduledThreadPoolExecutorWrapper(threadPoolName, (ScheduledThreadPoolExecutor) threadPoolExecutor);
            } else {
                manageable = new ThreadPoolExecutorWrapper(threadPoolName, threadPoolExecutor);
                //ScheduledThreadPoolExecutor 不代理queue，所以不支持ThreadLocal很好的传递，重点处理ThreadPoolExecutor
                //线程池内创建线程的来源只有一个，那就是增加worker的时候，而worker的增加需要ThreadFactory的包装
                //入队的线程，包括runnable和callable就是简单的入队操作，callable会包装成runnable入队
                //所以要实现threadLocal的传递只需要包装ThreadFactory和queue入队，塞入要传递的threadLocal就可以了。
                BlockingQueue wrapperQueue = QueueWrapper.wrapper(threadPoolExecutor.getQueue(), threadExecuteHook, threadPoolName);
                try {
                    modifyFinal(threadPoolExecutor, "workQueue", wrapperQueue);
                } catch (Exception e) {
                    log.warn("warn: inject queue error ->{}, threadLocal transmit invalid", e.getMessage());
                }
            }
            threadPoolExecutor.setThreadFactory(new ThreadFactoryWrapper(threadPoolExecutor.getThreadFactory(), threadExecuteHook, threadPoolName));
            manageableMap.put(threadPoolName, manageable);
            start();
            log.info("threadPoolName->{} registered", threadPoolName);
        } else {
            log.warn("threadPoolName->{} has been registered,skipped", threadPoolName);
        }
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
        Field field = null;
        if (object.getClass().equals(ThreadPoolExecutor.class)) {
            field = object.getClass().getDeclaredField(fieldName);
        } else if (object.getClass().equals(ScheduledThreadPoolExecutor.class)) {
            //获取父类
            field = object.getClass().getSuperclass().getDeclaredField(fieldName);
        }
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
                    if (Objects.nonNull(applicationContext)) {
                        try {
                            Map<String, Reporter> beansOfType = applicationContext.getBeansOfType(Reporter.class);
                            if (!CollectionUtils.isEmpty(beansOfType)) {
                                beansOfType.forEach((k, v) -> {
                                    try {
                                        v.report(tempList);
                                    } catch (Exception e) {
                                        log.error("report error ,name->{},error->{}", k, e);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            log.error("get beans from applicationContext error ,applicationContext is not refreshed or closed ,please check ,error->{}", e);
                        }
                    }
                }
                tempList.clear();
            } catch (Exception e) {
                log.error("report error->{}", e);
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
        ThreadExecuteHook bean = applicationContext.getBean(ThreadExecuteHook.class);
        if (Objects.nonNull(bean)) {
            ThreadPoolRegistry.defaultExecuteHook.setThreadExecuteHook(bean);
        }
    }

    /**
     * 包装容器内的threadPool
     */
    private void wrapperContext() {
        ThreadPoolProperties bean = applicationContext.getBean(ThreadPoolProperties.class);
        if (Objects.nonNull(bean)) {
            if (bean.isWrapper()) {
                Map<String, ThreadPoolExecutor> threadPools = applicationContext.getBeansOfType(ThreadPoolExecutor.class);
                if (!CollectionUtils.isEmpty(threadPools)) {
                    threadPools.forEach((k, v) -> registerThreadPool(k, v));
                }
                Map<String, ScheduledThreadPoolExecutor> scheduledPools = applicationContext.getBeansOfType(ScheduledThreadPoolExecutor.class);
                if (!CollectionUtils.isEmpty(scheduledPools)) {
                    scheduledPools.forEach((k, v) -> registerThreadPool(k, v));
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
    public void eventListener(ApplicationEvent event) {
        /**
         * 在收到EnvironmentChangeEvent会重新刷新属性
         * 但是如果直接从容器取可能还未刷新完成，
         * 所以就主动刷新一次，ConfigurationPropertiesRebinder
         * 也是相同的操作，其实所有的@ConfigurationProperties
         * 配置变量都需要spring容器处理，所以无论从什么地方发起的配置
         * 如，nacos，apollo，spring-config,zk,file
         * 都需要更新到容器的Environment中，然后走属性刷新
         * 无需关心配置是从何而来，只需要取到的是最新的配置即可

         ThreadPoolProperties bean = ProxyUtils.getTargetObject(applicationContext.getBean(ThreadPoolProperties.class));
         applicationContext.getAutowireCapableBeanFactory().destroyBean(bean);
         applicationContext.getAutowireCapableBeanFactory().initializeBean(bean, "threadPoolProperties");
         或者采用如下方式获取当前最新的配置即可
         ThreadPoolProperties threadPoolProperties=new ThreadPoolProperties();
         Binder.get(applicationContext.getEnvironment()).bind("fuled.dynamic.threadpool", Bindable.ofInstance(threadPoolProperties));
         */
        if (Objects.nonNull(event) && (event instanceof EnvironmentChangeEvent || "ConfigChangeEvent".equals(event.getClass().getSimpleName()))) {
            ThreadPoolProperties bean = applicationContext.getBean(ThreadPoolProperties.class);
            if (!CollectionUtils.isEmpty(bean.getConfig())) {
                bean.getConfig().forEach((k, v) -> {
                    updateCoreSize(k, v.getCoreSize());
                });
            }
        }
    }

    /**
     * stop all
     */
    private void stop() {
        if (!CollectionUtils.isEmpty(manageableMap)) {
            manageableMap.forEach((k, v) -> {
                v.shutdown();
                log.info("ThreadPool shutdown threadPoolName->{}", k);
            });
        }
    }

    /**
     * 处理容器，刷新和注册钩子
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        wrapperContext();
        eventListener(new EnvironmentChangeEvent(new HashSet<>()));
        Runtime.getRuntime().addShutdownHook(new Thread("threadShutdownHook") {
            @Override
            public void run() {
                ThreadPoolRegistry.this.stop();
            }
        });
    }
}
