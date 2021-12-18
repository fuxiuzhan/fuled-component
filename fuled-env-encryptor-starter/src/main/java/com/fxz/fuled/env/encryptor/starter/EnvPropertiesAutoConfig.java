package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.converter.ValueConverter;
import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * @author fxz
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvPropertiesAutoConfig implements EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Bean
    @Order()
    BeanFactoryPostProcessor envPropertiesProcessor(ValueConverter valueConverter) {
        return new EnvPropertiesProcessor(environment, valueConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    ValueConverter defaultValueConverter() {
        return new DefaultValueConverter();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Bean("ConfigEncryptVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-config-encrypt.version", "1.1.0.waterdrop", "fuled-config-encrypt-component");
    }
}