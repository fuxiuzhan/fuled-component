package com.fxz.fuled.threadpool.monitor.starter;


import com.fxz.fuled.threadpool.monitor.ThreadPoolRegistry;
import org.springframework.context.annotation.Import;

/**
 * spring 自动配置
 *
 * @author fxz
 */
@Import(ThreadPoolRegistry.class)
public class AutoConfigration {
}
