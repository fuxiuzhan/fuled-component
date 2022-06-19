package com.fxz.fuled.threadpool.monitor.starter;


import com.fxz.fuled.threadpool.monitor.ThreadPoolRegistry;
import com.fxz.fuled.threadpool.monitor.pojo.ThreadPoolProperties;
import org.springframework.context.annotation.Import;

/**
 * spring 自动配置
 *
 * @author fxz
 */
@Import({ThreadPoolRegistry.class, ThreadPoolProperties.class})
public class AutoConfiguration {
}
