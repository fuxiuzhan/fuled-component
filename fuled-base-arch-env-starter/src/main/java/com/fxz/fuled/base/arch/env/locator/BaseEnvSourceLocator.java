package com.fxz.fuled.base.arch.env.locator;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class BaseEnvSourceLocator implements PropertySourceLocator {
    @Override
    public PropertySource<?> locate(Environment environment) {
        return null;
    }
}
