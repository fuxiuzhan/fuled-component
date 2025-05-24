package com.fxz.fuled.base.arch.env.locator;

import com.alibaba.nacos.api.config.ConfigService;
import com.fxz.fuled.base.arch.env.Constant;
import com.fxz.fuled.config.starter.nacos.NacosConfigManager;
import com.fxz.fuled.config.starter.nacos.NacosConfigProperties;
import com.fxz.fuled.config.starter.nacos.property.NacosPropertySource;
import com.fxz.fuled.config.starter.nacos.property.NacosPropertySourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class BaseEnvSourceLocator implements PropertySourceLocator {

    private static final String ENV_NAME = "base_arch";


    private static final String DOT = ".";

    private NacosPropertySourceBuilder nacosPropertySourceBuilder;

    private NacosConfigProperties nacosConfigProperties;

    private NacosConfigManager nacosConfigManager;


    public BaseEnvSourceLocator(NacosConfigManager nacosConfigManager) {
        this.nacosConfigManager = nacosConfigManager;
        this.nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        ConfigService configService = nacosConfigManager.getConfigService();
        if (null == configService) {
            log.warn("no instance of config service found, can't load config from nacos");
            return null;
        }
        long timeout = nacosConfigProperties.getTimeout();
        nacosPropertySourceBuilder = new NacosPropertySourceBuilder(configService, timeout);
        CompositePropertySource composite = new CompositePropertySource(ENV_NAME);
        String appName = environment.getProperty("spring.application.name");
        composite.addFirstPropertySource(loadConfig(Constant.BASE_DATA_ID_GLOBAL));
        if (!StringUtils.isEmpty(appName)) {
            composite.addFirstPropertySource(loadConfig(appName));
        }
        return composite;
    }


    private NacosPropertySource loadConfig(String dataId) {
        NacosPropertySource build = nacosPropertySourceBuilder.build(dataId + DOT + Constant.ENV_FORMAT, Constant.BASE_GROUP, Constant.ENV_FORMAT, Boolean.TRUE);
        log.info("load config from nacos dataId->{} complete", dataId);
        return build;
    }
}
