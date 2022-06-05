package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.converter.ValueConverter;
import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author fxz
 */
@Configuration
public class EnvPropertiesAutoConfig {
    @Bean
    BeanFactoryPostProcessor envPropertiesProcessor(ConfigurableEnvironment environment, ValueConverter valueConverter) {
        return new EnvPropertiesProcessor(environment, valueConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    ValueConverter defaultValueConverter() {
        return new DefaultValueConverter();
    }

    @Bean("ConfigEncryptVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-config-encrypt.version", "1.1.0.waterdrop", "fuled-config-encrypt-component");
    }
}