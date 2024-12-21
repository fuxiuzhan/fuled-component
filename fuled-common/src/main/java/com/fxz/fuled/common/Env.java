package com.fxz.fuled.common;

/**
 * 配置管理应该尽可能的简单
 * 可以使用配置分层管理的方式
 * <p>
 * -Denv=dev
 * 启动时默认加载dev公共配置集
 * 此公共配置集由鸡架管理，配置包括：注册配置，监控配置，rpc配置等
 * <p>
 * 应用配置用法不变，则配置结构为
 * <p>
 * 1、公共配置
 * 2、应用配置
 * <p>
 * 应用配置优先级>公共配置
 */
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
