package com.fxz.fuled.config.starter.nacos.property;

import com.fxz.fuled.config.starter.nacos.NacosConfigProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NacosPropertySourceRepository {

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

    public static String getMapKey(String dataId, String group) {
        return String.join(NacosConfigProperties.COMMAS, String.valueOf(dataId),
                String.valueOf(group));
    }

}
