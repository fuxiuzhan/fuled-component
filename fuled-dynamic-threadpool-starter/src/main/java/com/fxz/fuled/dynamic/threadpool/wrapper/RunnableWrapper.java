package com.fxz.fuled.dynamic.threadpool.wrapper;

import com.fxz.fuled.dynamic.threadpool.RpcContext;
import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author fxz
 */
@Slf4j
public class RunnableWrapper implements Runnable, TaskWrapper {
    private Object meta;
    private Runnable runnable;

    private ThreadExecuteHook threadExecuteHook;

    /**
     * 存储父线程的tl
     */
    private Object parentThreadLocalMap;

    /**
     * 存储父线程的itl
     */
    private Object parentIThreadLocalMap;

    /**
     * 存储备份的tl
     */
    private Object backTheadLocalMap;
    /**
     * 存储备份的itl
     */
    private Object backIThreadLocalMap;

    @Getter
    private String threadPoolName;
    /*
     * 统计运行及等待时间需要排除worker的情况
     * 不然指标会体现出部分线程等待时间短，但
     * 运行时间极长
     */
    @Getter
    private long bornTs;
    @Getter
    private long executeTs;
    @Getter
    private long queuedDuration;
    @Getter
    private long executeDuration;
    @Getter
    private long completeTs;
    @Getter
    private boolean isWorker;


    public RunnableWrapper(Runnable runnable, Object meta, ThreadExecuteHook threadExecuteHook, String threadPoolName, boolean isWroker) {
        this.meta = meta;
        this.runnable = runnable;
        this.threadExecuteHook = threadExecuteHook;
        this.threadPoolName = threadPoolName;
        this.isWorker = isWroker;
        this.bornTs = System.currentTimeMillis();
        storeThreadLocal();
        threadExecuteHook.enqueue(this);
    }

    @Override
    public void run() {
        try {
            executeTs = System.currentTimeMillis();
            queuedDuration = executeTs - bornTs;
            //将threadLocal设置在hook可见范围内
            //backup线程池内线程的tl & itl
            backUpAndClearThreadLocal();
            //设置父线程的tl & itl
            setStoreThreadLocal();
            RpcContext.set(meta);
            //此处增加方法即可实现如下两个只有继承线程池才能实现的方法
            //beforeExecute
            threadExecuteHook.beforeExecute(this);
            runnable.run();
        } catch (Throwable throwable) {
            threadExecuteHook.onException(this, throwable);
        } finally {
            /**
             * 排除worker的干扰
             */
            if (!isWorker) {
                completeTs = System.currentTimeMillis();
                executeDuration = completeTs - executeTs;
            }
            threadExecuteHook.afterExecute(this);
            RpcContext.remove();
            //恢复线程池原始的tl & itl
            clearAndRecoverBackupThreadLocal();
            //afterExecute
        }
    }

    /**
     * 备份当前线程的tl & itl 并清理
     */
    private void backUpAndClearThreadLocal() {
        if (threadExecuteHook.threadLocalSupport()) {
            //backup tl
            backTheadLocalMap = getThreadLocal();
            //backup itl
            backIThreadLocalMap = getInheritThreadLocal();
            //clean tl
            cleanThreadLocal(Boolean.FALSE);
            //clean itl
            cleanThreadLocal(Boolean.TRUE);
        }
    }

    /**
     * 存储父线程的tl和itl数据
     */
    private void storeThreadLocal() {
        if (threadExecuteHook.threadLocalSupport()) {
            //将父线程的tl & itl 备份起来
            parentThreadLocalMap = getThreadLocal();
            parentIThreadLocalMap = getInheritThreadLocal();
        }
    }

    /**
     * 设置父线程的tl & itl
     */
    private void setStoreThreadLocal() {
        if (threadExecuteHook.threadLocalSupport()) {
            //set tl
            setThreadLocalMap(parentThreadLocalMap);
            //set itl
            setIThreadLocalMap(parentIThreadLocalMap);
        }
    }

    /**
     * 清理当前线程的tl & itl 并恢复原始的
     */
    private void clearAndRecoverBackupThreadLocal() {
        if (threadExecuteHook.threadLocalSupport()) {
            //clean tl
            cleanThreadLocal(Boolean.FALSE);
            //clean itl
            cleanThreadLocal(Boolean.TRUE);
            //set backup tl
            setThreadLocalMap(backTheadLocalMap);
            //set backup itl
            setIThreadLocalMap(backIThreadLocalMap);
        }
    }

    /**
     * 获取当前线程的threadLocal
     *
     * @return
     */
    private Object getThreadLocal() {
        return getThreadLocal(Boolean.FALSE);
    }

    /**
     * 获取当前线程的InheritThreadLocal
     *
     * @return
     */
    private Object getInheritThreadLocal() {
        return getThreadLocal(Boolean.TRUE);
    }

    /**
     * set tl
     *
     * @param threadLocalMap
     */
    private void setThreadLocalMap(Object threadLocalMap) {
        updateThreadLocal(threadLocalMap, Boolean.FALSE);
    }

    /**
     * set itl
     *
     * @param threadLocalMap
     */
    private void setIThreadLocalMap(Object threadLocalMap) {
        updateThreadLocal(threadLocalMap, Boolean.TRUE);
    }

    /**
     * 获取threadLocal
     *
     * @param inheritable
     * @return
     */
    private Object getThreadLocal(boolean inheritable) {
        String filed = "threadLocals";
        if (inheritable) {
            filed = "inheritableThreadLocals";
        }
        Thread parent = Thread.currentThread();
        Field threadLocals = ReflectionUtils.findField(Thread.class, filed);
        if (!threadLocals.isAccessible()) {
            ReflectionUtils.makeAccessible(threadLocals);
        }
        try {
            if (inheritable) {
                //itl
                //inheritableThreadLocals
                Object threadLocalObj = threadLocals.get(parent);
                if (Objects.nonNull(threadLocalObj)) {
                    // 利用ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) 生成新的
                    Optional<Method> createInheritedMapOpt = Arrays.stream(ThreadLocal.class.getDeclaredMethods()).filter(e -> e.getName().equals("createInheritedMap")).findFirst();
                    Method createInheritedMap = createInheritedMapOpt.get();
                    ReflectionUtils.makeAccessible(createInheritedMap);
                    return createInheritedMap.invoke(threadLocalObj.getClass(), threadLocalObj);
                }

            } else {
                //ThreadLocal 需要手动copy，
                //首先获取所有的entry对象
                //然后创建一个新的threadLocal，将entry对象都放进去
                //tl
                Object threadLocalObj = threadLocals.get(parent);
                if (Objects.nonNull(threadLocalObj)) {
                    //copy table
                    Field tableFiled = ReflectionUtils.findField(threadLocalObj.getClass(), "table");
                    ReflectionUtils.makeAccessible(tableFiled);
                    Object tableObj = tableFiled.get(threadLocalObj);
                    if (Objects.nonNull(tableObj)) {
                        Object threadLocalMap = null;
                        Optional<Method> setOpt = Arrays.stream(threadLocalObj.getClass().getDeclaredMethods()).filter(e -> e.getName().equals("set")).findFirst();
                        Method setMethod = setOpt.get();
                        setMethod.setAccessible(Boolean.TRUE);
                        WeakReference<ThreadLocal<?>>[] entries = (WeakReference<ThreadLocal<?>>[]) tableObj;
                        for (WeakReference<ThreadLocal<?>> entry : entries) {
                            if (Objects.nonNull(entry)) {
                                ThreadLocal<?> threadLocal = entry.get();
                                if (Objects.nonNull(threadLocal)) {
                                    Object value = threadLocal.get();
                                    if (Objects.isNull(threadLocalMap)) {
                                        //首次需要初始化
                                        Constructor<?> constructor = threadLocalObj.getClass().getDeclaredConstructor(ThreadLocal.class, Object.class);
                                        constructor.setAccessible(Boolean.TRUE);
                                        threadLocalMap = constructor.newInstance(threadLocal, value);
                                    } else {
                                        setMethod.invoke(threadLocalMap, threadLocal, value);
                                    }
                                }
                            }
                        }
                        return threadLocalMap;
                    }
                }
            }
        } catch (Exception e) {
            log.error("copy threadLocal from parentThread error->{}", e);
        }
        return null;
    }

    /**
     * 更新threadLocal
     *
     * @param threadLocal
     * @param inheritable
     */
    private void updateThreadLocal(Object threadLocal, boolean inheritable) {
        String filed = "threadLocals";
        if (inheritable) {
            filed = "inheritableThreadLocals";
        }
        Field threadLocals = ReflectionUtils.findField(Thread.class, filed);
        if (!threadLocals.isAccessible()) {
            ReflectionUtils.makeAccessible(threadLocals);
        }
        try {
            //type check
            threadLocals.set(Thread.currentThread(), threadLocal);
        } catch (Exception e) {
            log.info("clean threadLocal error, threadName->{},error->{}", Thread.currentThread().getName(), e);
        }
    }

    /**
     * 清理当前线程的threadLocal
     *
     * @param inheritable
     */
    private void cleanThreadLocal(boolean inheritable) {
        updateThreadLocal(null, inheritable);
    }

    @Override
    public long queuedDuration() {
        return queuedDuration;
    }

    @Override
    public long executedDuration() {
        return executeDuration;
    }
}
