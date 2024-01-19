package com.fxz.fuled.common;

public enum Env {
    CUS("Custom Config"),
    LOCAL("http://127.0.0.1:8848"),
    DEV("http://192.168.10.201:8848"),
    TEST("http://test.nacos.fuled.xyz:8848"),
    PRE("http://pre.nacos.fuled.xyz:8848"),
    GRAY("http://gray.nacos.fuled.xyz:8848"),
    PRD("http://prd.nacos.fuled.xyz:8848");
    private String configServer;

    public String getConfigServer() {
        return configServer;
    }

    Env(String domain) {
        this.configServer = domain;
    }
}
