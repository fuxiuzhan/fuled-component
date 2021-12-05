package com.fxz.fuled.name.starter;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;

@ConditionalOnDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
public class NameAutoConfig {

    @Bean
    public NacosServiceDiscovery nacosServiceDiscovery(NacosDiscoveryProperties discoveryProperties, NacosServiceManager nacosServiceManager) {
        return new NacosServiceDiscovery(discoveryProperties, nacosServiceManager);
    }
}
