package com.fxz.fuled.threadpool.monitor;

import com.fxz.fuled.threadpool.monitor.wrapper.ThreadFactoryWrapper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MEMO：
 * <p>
 * 非agent实现
 * <p>
 * 主要实现对线程池的动态控制及监控
 * <p>
 * 具体作用：
 * 1、对线程池参数可通过配置中心进行动态调整
 * <p>
 * 2、对线程池的执行情况进行上报，对接监控
 * <p>
 * 3、对容器内的线程池进行自动注册，支持线程池的手动注册
 * <p>
 * 4、在对线程池进行注册时支持钩子注册，便于配置更改的回调
 * <p>
 * 5、注册时包装线程池支持threadLocal上线文的传递
 */
public class AppMain {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        ThreadPoolRegistry.registerThreadPool("test", threadPoolExecutor);
        RpcContext.set("1");
        threadPoolExecutor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("rpc->" + RpcContext.get());
        });
        RpcContext.set("2");
        threadPoolExecutor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("rpc->" + RpcContext.get());
        });
        RpcContext.set("3");
        threadPoolExecutor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("rpc->" + RpcContext.get());
        });
        RpcContext.set("4");
        threadPoolExecutor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("rpc->" + RpcContext.get());
        });
        RpcContext.set("5");
    }
}
