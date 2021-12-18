package com.fxz.fuled.config.starter.nacos.property;

import com.fxz.fuled.config.starter.nacos.NacosConfigProperties;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NacosPropertySource extends MapPropertySource {

    /**
     * Nacos Group.
     */
    private final String group;

    /**
     * Nacos dataID.
     */
    private final String dataId;

    /**
     * timestamp the property get.
     */
    private final Date timestamp;

    /**
     * Whether to support dynamic refresh for this Property Source.
     */
    private final boolean isRefreshable;

    private Map<String, Object> source;
    NacosPropertySource(String group, String dataId, Map<String, Object> source,
                        Date timestamp, boolean isRefreshable) {
        super(String.join(NacosConfigProperties.COMMAS, dataId, group), source);
        this.group = group;
        this.dataId = dataId;
        this.timestamp = timestamp;
        this.isRefreshable = isRefreshable;
        this.source= source;
    }

    NacosPropertySource(List<PropertySource<?>> propertySources, String group,
                        String dataId, Date timestamp, boolean isRefreshable) {
        this(group, dataId, getSourceMap(group, dataId, propertySources), timestamp,
                isRefreshable);
    }

    private static Map<String, Object> getSourceMap(String group, String dataId,
                                                    List<PropertySource<?>> propertySources) {
        if (CollectionUtils.isEmpty(propertySources)) {
            return Collections.emptyMap();
        }
        // If only one, return the internal element, otherwise wrap it.
        if (propertySources.size() == 1) {
            PropertySource propertySource = propertySources.get(0);
            if (propertySource != null && propertySource.getSource() instanceof Map) {
                return (Map<String, Object>) propertySource.getSource();
            }
        }
        // If it is multiple, it will be returned as it is, and the internal elements
        // cannot be directly retrieved, so the user needs to implement the retrieval
        // logic by himself
        return Collections.singletonMap(
                String.join(NacosConfigProperties.COMMAS, dataId, group),
                propertySources);
    }

    public String getGroup() {
        return this.group;
    }

    public String getDataId() {
        return dataId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isRefreshable() {
        return isRefreshable;
    }

    @Override
    public Map<String, Object> getSource() {
        return source;
    }
}
