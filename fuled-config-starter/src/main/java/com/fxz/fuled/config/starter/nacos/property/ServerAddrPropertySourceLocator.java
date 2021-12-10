package com.fxz.fuled.config.starter.nacos.property;

import com.fxz.fuled.common.ConfigUtil;
import com.fxz.fuled.common.Env;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Properties;

/**
 * rewrite env
 * @author fxz
 */
@Configuration
public class ServerAddrPropertySourceLocator implements PropertySourceLocator {
    @Override
    public PropertySource<?> locate(Environment environment) {
        Properties properties = new Properties();
        ConfigUtil.initialize();
        Env env = ConfigUtil.getEnv();
        properties.put("spring.cloud.nacos.server-addr", env.getConfigServer() + ":" + env.getPort());
        properties.put("spring.cloud.nacos.config.namespace",env.name());
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("test-properties", properties);
        return propertiesPropertySource;
    }
}
