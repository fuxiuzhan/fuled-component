package com.fxz.fuled.env.encryptor.starter.wrapper;

import com.fxz.fuled.common.converter.ValueConverter;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.env.PropertySource;

/**
 * @author fxz
 */
public class EncryptablePropertySourceWrapper<T> extends PropertySource<T> implements EncryptablePropertySource {

    private PropertySource source;
    private ValueConverter valueConverter;

    public EncryptablePropertySourceWrapper(PropertySource source, ValueConverter valueConverter) {
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
    public Origin getOrigin(Object key) {
        if (getDelegate() instanceof OriginLookup) {
            return ((OriginLookup<String>) getDelegate()).getOrigin((String) key);
        }
        return null;
    }
}
