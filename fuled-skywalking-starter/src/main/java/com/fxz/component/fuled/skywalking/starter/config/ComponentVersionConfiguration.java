package com.fxz.component.fuled.skywalking.starter.config;

import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fxz
 */
@Configuration
public class ComponentVersionConfiguration {
    @Bean("skywalkingComponentVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-skywalking-component.version", "1.0.0.waterdrop", "fuled-skywalking-component");
    }
}
