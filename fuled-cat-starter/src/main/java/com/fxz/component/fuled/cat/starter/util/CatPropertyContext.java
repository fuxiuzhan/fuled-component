package com.fxz.component.fuled.cat.starter.util;


import com.dianping.cat.Cat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fxz
 */
public class CatPropertyContext implements Cat.Context {
    private Map<String, String> properties = new HashMap();

    public CatPropertyContext() {
    }

    @Override
    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        return (String) this.properties.get(key);
    }
}
