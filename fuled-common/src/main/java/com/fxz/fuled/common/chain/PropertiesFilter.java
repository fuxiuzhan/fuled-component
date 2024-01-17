package com.fxz.fuled.common.chain;


public abstract class PropertiesFilter {

    public String name() {
        return "";
    }

    public int order() {
        return Integer.MAX_VALUE;
    }

    public String filterGroup() {
        return "";
    }

    public boolean enabled() {
        return Boolean.TRUE;
    }

}
