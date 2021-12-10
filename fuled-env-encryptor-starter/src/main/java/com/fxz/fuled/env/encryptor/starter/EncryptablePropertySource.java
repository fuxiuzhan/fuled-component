package com.fxz.fuled.env.encryptor.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;

import java.util.Objects;

@Slf4j
public class EncryptablePropertySource<T> extends PropertySource<T> {
    PropertySource<T> source;

    public EncryptablePropertySource(String name, PropertySource<T> source) {
        super(name, (T) source);
        this.source = source;
    }

    @Override
    public Object getProperty(String name) {
        Object property = source.getProperty(name);
        if (Objects.nonNull(property)) {
            //process value
            //for example encrypt or decrypt
            log.info("PropertySourceWrapper key->{},value->{}", name, property);
        }
        return property;
    }

    @Override
    public T getSource() {
        return (T) source;
    }
}