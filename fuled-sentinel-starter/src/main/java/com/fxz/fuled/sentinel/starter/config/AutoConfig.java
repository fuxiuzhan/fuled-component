package com.fxz.fuled.sentinel.starter.config;

import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.context.annotation.Bean;

public class AutoConfig {

    @Bean("sentinelVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-sentinel-starter.version", "1.0.0.waterdrop", "fuled-sentinel-starter");
    }
}
