package com.fxz.fuled.config.starter.nacos.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.fxz.fuled.config.starter.nacos.NacosConfigManager;
import com.fxz.fuled.config.starter.nacos.NacosConfigProperties;
import com.fxz.fuled.config.starter.nacos.property.NacosPropertySourceLocator;
import com.fxz.fuled.config.starter.nacos.property.ServerAddrPropertySourceLocator;
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
        return new NacosConfigManager(nacosConfigProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosPropertySourceLocator nacosPropertySourceLocator(
            NacosConfigManager nacosConfigManager) {
        return new NacosPropertySourceLocator(nacosConfigManager);
    }

    @Bean
    public ServerAddrPropertySourceLocator serverAddrPropertySourceLocator() {
        return new ServerAddrPropertySourceLocator();
    }
    @Bean
    public ConfigService configService(NacosConfigManager configManager){
        ConfigService configService = configManager.getConfigService();
//        configService.addListener(ConfigUtil.getAppId(), properties.getGroup(), new NacosListener(ConfigUtil.getAppId(), propertyConverter));
        return configService;
    }
}
