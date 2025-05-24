package com.fxz.fuled.base.arch.env.config;

import com.fxz.fuled.base.arch.env.locator.BaseEnvSourceLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
@Import(BaseEnvSourceLocator.class)
public class BaseEnvAutoConfig {
}
