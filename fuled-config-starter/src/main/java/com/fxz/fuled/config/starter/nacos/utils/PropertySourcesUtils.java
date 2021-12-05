package com.fxz.fuled.config.starter.nacos.utils;

import org.springframework.core.env.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class PropertySourcesUtils {
    public PropertySourcesUtils() {
    }

    public static Map<String, Object> getSubProperties(Iterable<PropertySource<?>> propertySources, String prefix) {
        MutablePropertySources mutablePropertySources = new MutablePropertySources();
        Iterator var3 = propertySources.iterator();

        while (var3.hasNext()) {
            PropertySource<?> source = (PropertySource) var3.next();
            mutablePropertySources.addLast(source);
        }

        return getSubProperties((PropertySources) mutablePropertySources, prefix);
    }

    public static Map<String, Object> getSubProperties(ConfigurableEnvironment environment, String prefix) {
        return getSubProperties(environment.getPropertySources(), environment, prefix);
    }

    public static String normalizePrefix(String prefix) {
        return prefix.endsWith(".") ? prefix : prefix + ".";
    }

    public static Map<String, Object> getSubProperties(PropertySources propertySources, String prefix) {
        return getSubProperties(propertySources, new PropertySourcesPropertyResolver(propertySources), prefix);
    }

    public static Map<String, Object> getSubProperties(PropertySources propertySources, PropertyResolver propertyResolver, String prefix) {
        Map<String, Object> subProperties = new LinkedHashMap();
        String normalizedPrefix = normalizePrefix(prefix);
        Iterator iterator = propertySources.iterator();

        while (iterator.hasNext()) {
            PropertySource<?> source = (PropertySource) iterator.next();
            String[] var7 = getPropertyNames(source);
            int var8 = var7.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                String name = var7[var9];
                if (!subProperties.containsKey(name) && name.startsWith(normalizedPrefix)) {
                    String subName = name.substring(normalizedPrefix.length());
                    if (!subProperties.containsKey(subName)) {
                        Object value = source.getProperty(name);
                        if (value instanceof String) {
                            value = propertyResolver.resolvePlaceholders((String) value);
                        }

                        subProperties.put(subName, value);
                    }
                }
            }
        }

        return Collections.unmodifiableMap(subProperties);
    }

    public static String[] getPropertyNames(PropertySource propertySource) {
        String[] propertyNames = propertySource instanceof EnumerablePropertySource ? ((EnumerablePropertySource) propertySource).getPropertyNames() : null;
        if (propertyNames == null) {
            propertyNames = new String[]{};
        }

        return propertyNames;
    }
}
