package com.fxz.fuled.common.utils;


import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiuzhan.fu
 */
public class ParallelUtil {

    static int CORES = Runtime.getRuntime().availableProcessors();
    /**
     * io密集型可以使用更多的线程数
     * 　Ncpu = CPU的数量
     * 　　Ucpu = 目标CPU的使用率， 0 <= Ucpu <= 1
     * 　　W/C = 等待时间与计算时间的比率
     * 　　为保持处理器达到期望的使用率，最优的池的大小等于：
     * 　　Nthreads = Ncpu x Ucpu x (1 + W/C)
     * 算了，不折腾了，io线程池直接上16倍吧。
     */
    static ThreadPoolExecutor io_threadPool = new ThreadPoolExecutor(CORES * 16, CORES * 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), ThreadFactoryNamed.builder().namePrefix("io-thread-pool").build());
    static ThreadPoolExecutor com_threadPool = new ThreadPoolExecutor(CORES, CORES, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), ThreadFactoryNamed.builder().namePrefix("com-thread-pool").build());

    public static ThreadPoolExecutor getSingleThreadPool() {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
    }

    public static ThreadPoolExecutor getFixedThreadPool(int coreSize) {
        return new ThreadPoolExecutor(coreSize, coreSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(512), ThreadFactoryNamed.builder().namePrefix("fixed-thread-pool").build());
    }

    public static Future asyncInvoke(Object source, IProcess processor) {
        return asyncInvoke(source, processor, getSingleThreadPool(), true);
    }

    public static Future asyncInvoke(Object source, IProcess processor, ThreadPoolExecutor executor, boolean autoClose) {
        if (Objects.isNull(source)) {
            return null;
        }
        try {
            checkPrams(Arrays.asList(source), processor);
            return executor.submit(() -> processor.process(source));
        } finally {
            if (autoClose && !executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }

    public static <T> List<Result> parallelCompute(List<T> sources, IProcess processor, ThreadPoolExecutor executor, boolean autoClose) {
        if (CollectionUtils.isEmpty(sources)) {
            return null;
        }
        checkPrams(sources, processor);
        List<Result> results = new ArrayList<>();
        List<Future> futureList = new ArrayList<>();
        if (executor == null) {
            throw new RuntimeException("executor must not null");
        }
        for (int i = 0; i < sources.size(); i++) {
            int finalI = i;
            futureList.add(executor.submit(() -> processor.process(sources.get(finalI))));
        }
        for (int i = 0; i < futureList.size(); i++) {
            try {
                results.add(new Result(futureList.get(i).get(), true));
            } catch (Exception e) {
                results.add(new Result(null, false));
                e.printStackTrace();
            }
        }
        if (autoClose && !executor.isShutdown()) {
            executor.shutdown();
        }
        return results;
    }

    private static <T> void checkPrams(List<T> sources, IProcess processor) {

        if (processor == null) {
            throw new RuntimeException("processor must not null!");
        }
    }

    public static <T> List<Result> parallelCompute(List<T> sources, IProcess processor, boolean taskType) {
        if (CollectionUtils.isEmpty(sources)) {
            return null;
        }
        checkPrams(sources, processor);
        List<Result> results = new ArrayList<>();
        List<Future> futureList = new ArrayList<>();
        ThreadPoolExecutor usedPoolType = taskType ? com_threadPool : io_threadPool;
        for (int i = 0; i < sources.size(); i++) {
            int finalI = i;
            futureList.add(usedPoolType.submit(() -> processor.process(sources.get(finalI))));
        }
        for (int i = 0; i < futureList.size(); i++) {
            try {
                results.add(new Result(futureList.get(i).get(), true));
            } catch (Exception e) {
                results.add(new Result(null, false));
                e.printStackTrace();
            }
        }
        return results;
    }

    public static class Result<R> {
        R result;
        boolean status;

        public R getResult() {
            return result;
        }

        public boolean Status() {
            return status;
        }

        Result(R result, boolean status) {
            this.result = result;
            this.status = status;
        }
    }

    public interface IProcess<R> {
        /**
         * 实际方法调用
         *
         * @param obj
         * @return
         * @throws Exception
         */
        R process(Object obj) throws Exception;
    }
}

