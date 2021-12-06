package com.fxz.fuled.config.starter.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fxz.fuled.config.starter.enums.Env;
import com.fxz.fuled.config.starter.spring.util.ConfigUtil;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author fuled
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
public class NacosListenerRegister {
    @Bean
    public ConfigService configService(NacosConfigProperties properties, PropertyConverter propertyConverter) throws NacosException {
        ConfigUtil.initialize();
        Env env = ConfigUtil.getEnv();
        properties.setServerAddr(env.getConfigServer() + ":" + env.getPort());
        Properties config = properties.assembleConfigServiceProperties();
        ConfigService configService = NacosFactory.createConfigService(config);
        configService.addListener(ConfigUtil.getAppId(), properties.getGroup(), new NacosListener(ConfigUtil.getAppId(), propertyConverter));
        return configService;
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosConfigProperties nacosConfigProperties(ApplicationContext context) {
        if (context.getParent() != null
                && BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                context.getParent(), NacosConfigProperties.class).length > 0) {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(),
                    NacosConfigProperties.class);
        }
        return new NacosConfigProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertyConverter defaultConverter() {
        return new DefaultPropertyConverter();
    }
}
