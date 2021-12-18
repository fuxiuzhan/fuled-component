package com.fxz.fuled.common.version;

import java.util.Map;

/**
 * @author fxz
 * 版本管理，每个组件注入容器同时需要注入一个version
 */

public class ComponentVersion {

    private String name;

    private String version;

    private String desc;

    private Map<String, String> meta;

    public ComponentVersion(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public ComponentVersion(String name, String version, String desc) {
        this.name = name;
        this.version = version;
        this.desc = desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDesc() {
        return desc;
    }

    public Map<String, String> getMeta() {
        return meta;
    }
}
