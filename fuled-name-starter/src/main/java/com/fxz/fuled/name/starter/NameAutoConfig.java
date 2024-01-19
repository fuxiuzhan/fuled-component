package com.fxz.fuled.name.starter;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.fxz.fuled.common.Env;
import com.fxz.fuled.common.utils.ConfigUtil;
import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fxz
 * 重新的目的和配置中心一致
 * 为了修改nacos的连结地址，其他参数不变
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
        initEnv(discoveryProperties);
        return new NacosServiceDiscoveryWrapper(discoveryProperties, nacosServiceManager);
    }

    public static void initEnv(NacosDiscoveryProperties discoveryProperties) {
        ConfigUtil.initialize();
        Env env = ConfigUtil.getEnv();
        if (Env.CUS.equals(env)) {
            discoveryProperties.setServerAddr(env.getConfigServer());
            discoveryProperties.setNamespace(ConfigUtil.getEnv().name().toUpperCase());
            discoveryProperties.setService(ConfigUtil.getAppId());
        }
    }

    @Bean("namingVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-naming-config.version", "1.0.0.waterdrop", "fuled-naming-component");
    }
}
