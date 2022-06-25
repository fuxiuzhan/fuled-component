package com.fxz.fuled.threadpool.monitor.wrapper;

import com.fxz.fuled.threadpool.monitor.RpcContext;
import com.fxz.fuled.threadpool.monitor.manage.ThreadExecuteHook;
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
public class RunnableWrapper implements Runnable {
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

    public RunnableWrapper(Runnable runnable, Object meta, ThreadExecuteHook threadExecuteHook) {
        this.meta = meta;
        this.runnable = runnable;
        this.threadExecuteHook = threadExecuteHook;
        storeThreadLocal();
    }

    @Override
    public void run() {
        try {
            //将threadLocal设置在hook可见范围内
            //backup线程池内线程的tl & itl
            backUpAndClearThreadLocal();
            //设置父线程的tl & itl
            setStoreThreadLocal();
            RpcContext.set(meta);
            //此处增加方法即可实现如下两个只有继承线程池才能实现的方法
            //beforeExecute
            threadExecuteHook.beforeExecute(runnable);
            runnable.run();
        } catch (Throwable throwable) {
            threadExecuteHook.onException(runnable, throwable);
        } finally {
            threadExecuteHook.afterExecute(runnable);
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
        //backup tl
        backTheadLocalMap = getThreadLocal();
        //backup itl
        backIThreadLocalMap = getInheritThreadLocal();
        //clean tl
        cleanThreadLocal(Boolean.FALSE);
        //clean itl
        cleanThreadLocal(Boolean.TRUE);
    }

    /**
     * 存储父线程的tl和itl数据
     */
    private void storeThreadLocal() {
        //将父线程的tl & itl 备份起来
        parentThreadLocalMap = getThreadLocal();
        parentIThreadLocalMap = getInheritThreadLocal();
    }

    /**
     * 设置父线程的tl & itl
     */
    private void setStoreThreadLocal() {
        //set tl
        setThreadLocalMap(parentThreadLocalMap);
        //set itl
        setIThreadLocalMap(parentIThreadLocalMap);
    }

    /**
     * 清理当前线程的tl & itl 并恢复原始的
     */
    private void clearAndRecoverBackupThreadLocal() {
        //clean tl
        cleanThreadLocal(Boolean.FALSE);
        //clean itl
        cleanThreadLocal(Boolean.TRUE);
        //set backup tl
        setThreadLocalMap(backTheadLocalMap);
        //set backup itl
        setIThreadLocalMap(backIThreadLocalMap);
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
        if (Objects.nonNull(threadLocalMap)) {
            updateThreadLocal(threadLocalMap, Boolean.FALSE);
        } else {
            updateThreadLocal(null, Boolean.FALSE);
        }
    }

    /**
     * set itl
     *
     * @param threadLocalMap
     */
    private void setIThreadLocalMap(Object threadLocalMap) {
        if (Objects.nonNull(threadLocalMap)) {
            updateThreadLocal(threadLocalMap, Boolean.TRUE);
        } else {
            updateThreadLocal(null, Boolean.TRUE);
        }
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
                                Object value = threadLocal.get();
                                if (Objects.nonNull(value)) {
                                    if (Objects.isNull(threadLocalMap)) {
                                        //首次需要初始化
                                        Constructor<?> constructor = threadLocalObj.getClass().getDeclaredConstructor(ThreadLocal.class, Object.class);
                                        constructor.setAccessible(Boolean.TRUE);
                                        threadLocalMap = constructor.newInstance(threadLocal, value);
                                    } else {
                                        setMethod.invoke(threadLocalMap, threadLocal, value);
                                    }
                                    log.info("key->{} value->{}", threadLocal, value);
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
            if (Objects.isNull(threadLocal)) {
                threadLocals.set(Thread.currentThread(), null);
            } else {
                threadLocals.set(Thread.currentThread(), threadLocal);
            }
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
}
