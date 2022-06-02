package com.fxz.fuled.env.encryptor.starter.wrapper;

import com.fxz.fuled.common.converter.ValueConverter;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

public class EncryptableEnumerablePropertySourceWrapper<T> extends EnumerablePropertySource<T> implements EncryptablePropertySource {

    private PropertySource source;
    private ValueConverter valueConverter;

    public EncryptableEnumerablePropertySourceWrapper(PropertySource source, ValueConverter valueConverter) {
        super(source.getName(), (T) source.getSource());
        this.source = source;
        this.valueConverter = valueConverter;
    }

    @Override
    public PropertySource getDelegate() {
        return source;
    }

    @Override
    public ValueConverter getValueConveter() {
        return valueConverter;
    }

    @Override
    public Object getProperty(String name) {
        Object property = source.getProperty(name);
        return convert(property);
    }

    @Override
    public String[] getPropertyNames() {
        return ((EnumerablePropertySource) source).getPropertyNames();
    }
}
