package com.fxz.fuled.base.arch.env.config;

import com.fxz.fuled.base.arch.env.locator.BaseEnvSourceLocator;
import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
@Import(BaseEnvSourceLocator.class)
public class BaseEnvAutoConfig {

    @Bean("baseArchComponent")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-base-arch-env-starter.version", "1.1.0.waterdrop", "fuled-base-arch-env-starter");
    }

}
