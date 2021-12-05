package com.fxz.fuled.name.starter;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fxz
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
public class NameAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public NacosDiscoveryProperties nacosProperties() {
        return new NacosDiscoveryProperties();
    }

    @Bean
    public NacosServiceDiscovery nacosServiceDiscovery(NacosDiscoveryProperties discoveryProperties, NacosServiceManager nacosServiceManager) {
        return new NacosServiceDiscoveryWrapper(discoveryProperties, nacosServiceManager);
    }
}
