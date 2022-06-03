package com.fxz.fuled.env.encryptor.starter.wrapper;

import com.fxz.fuled.common.converter.ValueConverter;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.env.PropertySource;

import java.util.Objects;

/**
 * @author fxz
 */
public interface EncryptablePropertySource<T> extends OriginLookup<T> {

    /**
     * getDelegate
     *
     * @return
     */
    PropertySource<T> getDelegate();

    /**
     * getValueConveter
     *
     * @return
     */
    ValueConverter getValueConveter();

    default Origin getOrigin(Object key) {
        if (getDelegate() instanceof OriginLookup) {
            return ((OriginLookup<String>) getDelegate()).getOrigin(String.valueOf(key));
        }
        return null;
    }

    @Override
    default boolean isImmutable() {
        if (getDelegate() instanceof OriginLookup) {
            return ((OriginLookup<?>) getDelegate()).isImmutable();
        }
        return OriginLookup.super.isImmutable();
    }

    /**
     * convert
     *
     * @param value
     * @return
     */
    default Object convert(Object value) {
        if (Objects.nonNull(value)) {
            if (value instanceof String) {
                return getValueConveter().convert((String) value);
            }
        }
        return value;
    }
}
