package com.fxz.fuled.dynamic.threadpool.starter;


import com.fxz.fuled.common.dynamic.threadpool.reporter.Reporter;
import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.dynamic.threadpool.ThreadPoolRegistry;
import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;
import com.fxz.fuled.dynamic.threadpool.manage.impl.ThreadExecuteHookReporter;
import com.fxz.fuled.dynamic.threadpool.pojo.ThreadPoolProperties;
import com.fxz.fuled.dynamic.threadpool.reporter.DefaultReporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * spring 自动配置
 *
 * @author fxz
 */
@Import({ThreadPoolRegistry.class, ThreadPoolProperties.class})
public class AutoConfiguration {

    @Bean("dynamicThreadPoolVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-dynamic-threadpool.version", "1.0.0.waterdrop", "fuled-dynamic-threadpool-component");
    }

    @Bean
    @ConditionalOnMissingBean
    public Reporter defaultReporter() {
        return new DefaultReporter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadExecuteHook threadExecuteHook() {
        return new ThreadExecuteHookReporter();
    }
}
