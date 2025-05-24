package com.fxz.fuled.base.arch.env.locator;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class BaseEnvSourceLocator implements PropertySourceLocator {

    private static final String ENV_NAME = "base_arch";

    @Override
    public PropertySource<?> locate(Environment environment) {
        CompositePropertySource composite = new CompositePropertySource(
                ENV_NAME);
//        composite.addFirstPropertySource("base_data_id");
//        composite.addFirstPropertySource("app_data_id");
        return composite;
    }
}
