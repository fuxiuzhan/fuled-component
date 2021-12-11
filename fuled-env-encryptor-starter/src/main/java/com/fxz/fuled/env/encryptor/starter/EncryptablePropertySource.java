package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.converter.ValueConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;

import java.util.Objects;

/**
 * @author fxz
 */
@Slf4j
public class EncryptablePropertySource<T> extends PropertySource<T> {
    private PropertySource<T> source;
    private ValueConverter valueConverter;

    public EncryptablePropertySource(String name, PropertySource<T> source, ValueConverter valueConverter) {
        super(name, (T) source);
        this.source = source;
        this.valueConverter = valueConverter;
    }

    @Override
    public Object getProperty(String name) {
        Object property = source.getProperty(name);
        if (Objects.nonNull(property)) {
            //process value
            //for example encrypt or decrypt
            String converted = valueConverter.convert(property.toString());
            log.info("PropertySourceWrapper key->{},value->{}", name, converted);
            return converted;
        }
        return property;
    }

    @Override
    public T getSource() {
        return (T) source;
    }


}