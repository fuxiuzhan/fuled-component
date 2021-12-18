package com.fxz.fuled.config.starter.nacos.property;

import com.alibaba.nacos.api.config.PropertyChangeType;
import com.fxz.fuled.config.starter.nacos.NacosConfigProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NacosPropertySourceRepository {

    /**
     * 这里要吐槽一下 ConfigChangeItem  没有dataId和groupId等信息，比较尴尬
     * 使用event机制管理的方式无法区分具体更改的properties
     */
    public static final String WRITEABLE_PROPERTIES = "nacos-rewritable-properties";

    private final static ConcurrentHashMap<String, NacosPropertySource> NACOS_PROPERTY_SOURCE_REPOSITORY = new ConcurrentHashMap<>();

    private NacosPropertySourceRepository() {

    }

    /**
     * @return all nacos properties from application context.
     */
    public static List<NacosPropertySource> getAll() {
        return new ArrayList<>(NACOS_PROPERTY_SOURCE_REPOSITORY.values());
    }


    @Deprecated
    public static void collectNacosPropertySources(
            NacosPropertySource nacosPropertySource) {
        NACOS_PROPERTY_SOURCE_REPOSITORY.putIfAbsent(nacosPropertySource.getDataId(),
                nacosPropertySource);
    }


    public static void collectNacosPropertySource(NacosPropertySource nacosPropertySource) {
        NACOS_PROPERTY_SOURCE_REPOSITORY
                .putIfAbsent(getMapKey(nacosPropertySource.getDataId(),
                        nacosPropertySource.getGroup()), nacosPropertySource);
    }

    public static NacosPropertySource getNacosPropertySource(String dataId,
                                                             String group) {
        return NACOS_PROPERTY_SOURCE_REPOSITORY.get(getMapKey(dataId, group));
    }

    /**
     * 更新overlay下层的数据，这次overlay的设定要被打破了
     *
     * @param key
     * @param value
     * @param type
     */
    public static void updateExistsValue(String key, Object value, PropertyChangeType type) {
        for (NacosPropertySource nacosPropertySource : NACOS_PROPERTY_SOURCE_REPOSITORY.values()) {
            if (nacosPropertySource.getSource().containsKey(key)) {
                if (PropertyChangeType.DELETED.equals(type)) {
                    nacosPropertySource.getSource().remove(key);
                } else {
                    nacosPropertySource.getSource().put(key, value);
                }
            }
        }
    }

    public static String getMapKey(String dataId, String group) {
        return String.join(NacosConfigProperties.COMMAS, String.valueOf(dataId),
                String.valueOf(group));
    }

}
