package com.fxz.fuled.threadpool.monitor.starter;


import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.threadpool.monitor.ThreadPoolRegistry;
import com.fxz.fuled.threadpool.monitor.pojo.ThreadPoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * spring 自动配置
 *
 * @author fxz
 */
@Import({ThreadPoolRegistry.class, ThreadPoolProperties.class})
public class AutoConfiguration {

    @Bean("threadPoolMonitorVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-threadpool-monitor.version", "1.0.0.waterdrop", "fuled-threadpool-monitor-component");
    }
}
