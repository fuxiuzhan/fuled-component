package com.fxz.fuled.common;

public enum Env {
    DEV("http", "192.168.10.201", 8848),
    TEST("http", "test.nacos.fuled.xyz", 8848),
    PRE("http", "pre.nacos.fuled.xyz", 8848),
    GRAY("http", "gray.nacos.fuled.xyz", 8848),
    PRD("http", "prd.nacos.fuled.xyz", 8848);
    private String configServer;
    private int port;
    private String schema;

    public String getConfigServer() {
        return configServer;
    }

    public int getPort() {
        return port;
    }

    public String getSchema() {
        return schema;
    }

    Env(String schema, String domain, int port) {
        this.schema = schema;
        this.configServer = domain;
        this.port = port;
    }
}
