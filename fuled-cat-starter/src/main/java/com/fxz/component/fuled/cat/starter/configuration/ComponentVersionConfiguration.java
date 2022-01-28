package com.fxz.component.fuled.cat.starter.configuration;

import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fxz
 */
@Configuration
public class ComponentVersionConfiguration {
    @Bean("CatComponentVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-cat-component.version", "1.0.0.waterdrop", "fuled-cat-component");
    }
}
