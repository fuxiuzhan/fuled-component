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

/**
 * 使用 BootstrapConfiguration 接口启动，早于容器启动
 * 在有refreshEvent的情况下会重复初始化
 * 重写的目的是要控制nacos的连接信息
 */

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
        /**
         * 改写naocs连接信息，其他信息不变
         */
        Env envLocal = ConfigUtil.getEnv();
        if (!Env.CUS.equals(envLocal)) {
            nacosConfigProperties.setServerAddr(envLocal.getConfigServer());
            nacosConfigProperties.setNamespace(envLocal.name());
            //skip ,lazy set
            //nacosConfigProperties.setGroup(ConfigUtil.getAppId());
        }
        return new NacosConfigManager(nacosConfigProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosPropertySourceLocator nacosPropertySourceLocator(
            NacosConfigManager nacosConfigManager) {
        /**
         * 利用springboot提供的配置加载接口，其实还可以使用如下方式
         * 1.实现envAware接口进行加载
         * 2.ApplicationContextInitializer 方式加载
         * 3.beanPostProcessor方式加载(第一此触发时加载)
         * 4.SpringApplicationRunListener 方式加载
         * 其实实现的方式很多，但都是在spring容器执行refresh之前进行
         * 就可以，因为refresh后bean的配置就会确定下来了。
         * 但是就实现的优雅性而言，建议加载和更新分开，单独控制
         * 而不使用event来进行重复加载来达到更新的目的
         */
        return new NacosPropertySourceLocator(nacosConfigManager);
    }

    @Bean("diamondVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-diamond-config.version", "1.1.0.waterdrop", "fuled-config-component");
    }
}
