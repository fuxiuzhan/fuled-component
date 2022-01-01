package com.fxz.fuled.config.starter.nacos.config;

import com.fxz.fuled.common.Env;
import com.fxz.fuled.common.utils.ConfigUtil;
import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.config.starter.nacos.NacosConfigManager;
import com.fxz.fuled.config.starter.nacos.NacosConfigProperties;
import com.fxz.fuled.config.starter.nacos.property.NacosPropertySourceLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
public class NacosConfigBootstrapConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public NacosConfigProperties nacosConfigProperties() {
        return new NacosConfigProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosConfigManager nacosConfigManager(
            NacosConfigProperties nacosConfigProperties) {
        ConfigUtil.initialize();
        Env envLocal = ConfigUtil.getEnv();
        nacosConfigProperties.setServerAddr(envLocal.getConfigServer() + ":" + envLocal.getPort());
        nacosConfigProperties.setNamespace(envLocal.name());
        nacosConfigProperties.setGroup(ConfigUtil.getAppId().toUpperCase());
        return new NacosConfigManager(nacosConfigProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosPropertySourceLocator nacosPropertySourceLocator(
            NacosConfigManager nacosConfigManager) {
        return new NacosPropertySourceLocator(nacosConfigManager);
    }

    @Bean("diamondVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-diamond-config.version", "1.1.0.waterdrop", "fuled-config-component");
    }
}
