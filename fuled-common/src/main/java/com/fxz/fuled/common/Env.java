package com.fxz.fuled.common;

public enum Env {
    DEV("192.168.10.201", 8848),
    TEST("test.nacos.fuled.xyz", 8848),
    PRE("pre.nacos.fuled.xyz", 8848),
    GRAY("gray.nacos.fuled.xyz", 8848),
    PRD("81.68.198.67", 8848);
    private String configServer;
    private int port;

    public String getConfigServer() {
        return configServer;
    }

    public int getPort() {
        return port;
    }

    Env(String domain, int port) {
        this.configServer = domain;
        this.port = port;
    }
}
