package com.fxz.fuled.env.encryptor.starter.wrapper;

import com.fxz.fuled.common.converter.ValueConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.origin.Origin;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;

/**
 * @author fxz
 */
@Slf4j
public class EncryptableMapPropertySourceWrapper extends MapPropertySource implements EncryptablePropertySource {

    private PropertySource source;
    private ValueConverter valueConverter;

    public EncryptableMapPropertySourceWrapper(PropertySource source, ValueConverter valueConverter) {
        super(source.getName(), (Map<String, Object>) source.getSource());
        this.source = source;
        this.valueConverter = valueConverter;
    }

    @Override
    public Object getProperty(String name) {
        Object property = source.getProperty(name);
        return convert(property);
    }

    @Override
    public boolean isImmutable() {
        return EncryptablePropertySource.super.isImmutable();
    }

    @Override
    public PropertySource getDelegate() {
        return source;
    }

    @Override
    public ValueConverter getValueConveter() {
        return valueConverter;
    }
}
