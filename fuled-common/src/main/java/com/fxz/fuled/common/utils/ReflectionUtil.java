package com.fxz.fuled.common.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;

public final class ReflectionUtil {
    public static Object getFieldValue(Class<?> targetClass, String fieldName, Object targetObj) {
        Field field = getField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return null;
        }
        Object fieldObj = ReflectionUtils.getField(field, targetObj);
        if (Objects.isNull(fieldObj)) {
            return null;
        }
        return fieldObj;
    }

    public static void setFieldValue(Class<?> targetClass, String fieldName, Object targetObj, Object targetVal) throws IllegalAccessException {
        Field field = getField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return;
        }
        if (!field.isAccessible()) {
            field.setAccessible(Boolean.TRUE);
        }
        field.set(targetObj, targetVal);
    }

    public static Field getField(Class<?> targetClass, String fieldName) {
        Field field = ReflectionUtils.findField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return null;
        }
        ReflectionUtils.makeAccessible(field);
        return field;
    }
}
