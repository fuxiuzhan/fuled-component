package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.converter.ValueConverter;
import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author fxz
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "fuled.env.encryptor.enabled", matchIfMissing = true)
@Import({ApplicationEventListener.class})
public class EnvPropertiesAutoConfig {
    @Bean
    BeanFactoryPostProcessor envPropertiesProcessor(ConfigurableEnvironment environment, PropertiesConvertor propertiesConvertor) {
        return new EnvPropertiesProcessor(environment, propertiesConvertor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValueConverter defaultValueConverter(@Value("${fuled.env.encryptor.password:}") String password) {
        return new DefaultValueConverter(password);
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertiesConvertor propertiesConvertor(ValueConverter valueConverter) {
        return new PropertiesConvertor(valueConverter);
    }

    @Bean("configEncryptVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-config-encrypt.version", "1.1.0.waterdrop", "fuled-config-encrypt-component");
    }
}