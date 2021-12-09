package com.fxz.fuled.config.starter.nacos.property;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

public abstract class AbstractPropertySourceLoader  implements PropertySourceLoader {

    /**
     * symbol: dot.
     */
    static final String DOT = ".";


    /**
     * Load the resource into one or more property sources. Implementations may either
     * return a list containing a single source, or in the case of a multi-document format
     * such as yaml a source for each document in the resource.
     * @param name the root name of the property source. If multiple documents are loaded
     * an additional suffix should be added to the name for each source loaded.
     * @param resource the resource to load
     * @return a list property sources
     * @throws IOException if the source cannot be loaded
     */
    @Override
    public List<PropertySource<?>> load(String name, Resource resource){
        return null;
    }

    /**
     * Load the resource into one or more property sources. Implementations may either
     * return a list containing a single source, or in the case of a multi-document format
     * such as yaml a source for each document in the resource.
     * @param name the root name of the property source. If multiple documents are loaded
     * an additional suffix should be added to the name for each source loaded.
     * @param resource the resource to load
     * @return a list property sources
     * @throws IOException if the source cannot be loaded
     */
    protected abstract List<PropertySource<?>> doLoad(String name, Resource resource)
            throws IOException;

    protected void flattenedMap(Map<String, Object> result, Map<String, Object> dataMap,
                                String parentKey) {
        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, Object>> entries = dataMap.entrySet();
        for (Iterator<Map.Entry<String, Object>> iterator = entries.iterator(); iterator
                .hasNext();) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();

            String fullKey = StringUtils.isEmpty(parentKey) ? key : key.startsWith("[")
                    ? parentKey.concat(key) : parentKey.concat(DOT).concat(key);

            if (value instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) value;
                flattenedMap(result, map, fullKey);
                continue;
            }
            else if (value instanceof Collection) {
                int count = 0;
                Collection<Object> collection = (Collection<Object>) value;
                for (Object object : collection) {
                    flattenedMap(result,
                            Collections.singletonMap("[" + (count++) + "]", object),
                            fullKey);
                }
                continue;
            }

            result.put(fullKey, value);
        }
    }

}
