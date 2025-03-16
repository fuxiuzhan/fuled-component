package com.fxz.fuled.dynamic.datasource.starter.utils;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProxyUtils {
    /**
     * 获取所有父类及实现接口
     *
     * @param clazz
     * @return
     */
    public static Set<String> findAllSuperClass(Class clazz) {
        Set<Class<?>> allInterfacesForClassAsSet = ClassUtils.getAllInterfacesForClassAsSet(clazz);
        allInterfacesForClassAsSet.add(clazz);
        allInterfacesForClassAsSet.addAll(getAllSuperForClassAsSet(clazz));
        return allInterfacesForClassAsSet.stream().map(c -> c.getName()).collect(Collectors.toSet());
    }

    public static Set<Class<?>> getAllSuperForClassAsSet(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            return Collections.singleton(clazz);
        }
        Set<Class<?>> superClazz = new LinkedHashSet<>();
        Class<?> current = clazz;
        while (!superClazz.contains(current)) {
            Class cls = current.getSuperclass();
            superClazz.add(cls);
            current = current.getSuperclass();
        }
        return superClazz;
    }

    public static boolean isMatch(Set<String> packages, Set<String> targetPacks) {
        if (!CollectionUtils.isEmpty(targetPacks) && !CollectionUtils.isEmpty(packages)) {
            for (String aPackage : packages) {
                for (String targetPack : targetPacks) {
                    if (aPackage.startsWith(targetPack)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }
}
