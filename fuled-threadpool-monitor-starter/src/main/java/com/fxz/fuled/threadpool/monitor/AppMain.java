package com.fxz.fuled.threadpool.monitor;

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

}
