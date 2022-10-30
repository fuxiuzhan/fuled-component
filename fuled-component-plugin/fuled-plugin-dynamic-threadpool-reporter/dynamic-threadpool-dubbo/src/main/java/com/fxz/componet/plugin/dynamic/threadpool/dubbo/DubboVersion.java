package com.fxz.componet.plugin.dynamic.threadpool.dubbo;

import org.apache.dubbo.common.Version;

public class DubboVersion {

    private DubboVersion() {
    }

    public static final String VERSION_2_7_5 = "2.7.5";

    public static final String VERSION_3_0_3 = "3.0.3";

    public static int compare(String v1, String v2) {
        return Integer.compare(Version.getIntVersion(v1), Version.getIntVersion(v2));
    }
}
