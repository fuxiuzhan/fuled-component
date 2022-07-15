package com.fxz.fuled.sentinel.starter.config;

/**
 * @author fxz
 * <p>
 * sentinel 控制台地址
 */
public enum SentinelEnv {
    DEV("192.168.10.201:8858"),
    TEST("test.sentinel.fuled.xyz:8858"),
    PRE("pre.sentinel.fuled.xyz:8858"),
    GRAY("gray.sentinel.fuled.xyz:8858"),
    PRD("prd.sentinel.fuled.xyz:8858");
    private String dashboard;

    SentinelEnv(String dashboard) {
        this.dashboard = dashboard;
    }

    public String getDashboard() {
        return dashboard;
    }

    public static SentinelEnv getEnv() {
        SentinelEnv env = SentinelEnv.TEST;
        for (SentinelEnv value : SentinelEnv.values()) {
            if (value.name().equalsIgnoreCase(System.getProperty("env", ""))) {
                env = value;
                break;
            }
        }
        return env;
    }
}
