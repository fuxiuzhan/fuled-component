package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.converter.ValueConverter;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptableEnumerablePropertySourceWrapper;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptableMapPropertySourceWrapper;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptablePropertySource;
import com.fxz.fuled.env.encryptor.starter.wrapper.EncryptablePropertySourceWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;

import java.util.Objects;

@Configuration
public class PropertiesConvertor {
    private ValueConverter valueConverter;

    public PropertiesConvertor(ValueConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    public void convert(MutablePropertySources propertySources) {
        propertySources.forEach(ps -> {
            if (!(ps instanceof EncryptablePropertySource)) {
                PropertySource propertySource = null;
                if (ps instanceof SystemEnvironmentPropertySource) {
                } else if (ps instanceof PropertiesPropertySource) {
                    propertySource = new EncryptablePropertySourceWrapper<>(ps, valueConverter);
                } else if (ps instanceof MapPropertySource) {
                    propertySource = new EncryptableMapPropertySourceWrapper(ps, valueConverter);
                } else if (ps instanceof EnumerablePropertySource) {
                    propertySource = new EncryptableEnumerablePropertySourceWrapper<>(ps, valueConverter);
                }
                if (Objects.nonNull(propertySource)) {
                    propertySources.replace(ps.getName(), propertySource);
                }
            }
        });
    }
}
