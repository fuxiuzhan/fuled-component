package com.fxz.fuled.config.starter.nacos;

import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;
import com.fxz.fuled.config.starter.Config;
import com.fxz.fuled.config.starter.ConfigService;
import com.fxz.fuled.config.starter.model.ConfigChange;
import com.fxz.fuled.config.starter.spring.util.ApplicationContextUtil;
import com.fxz.fuled.config.starter.spring.util.ConfigUtil;
import com.fxz.fuled.config.starter.spring.util.SpringInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fuled
 */
public class NacosListener extends AbstractConfigChangeListener {
    private String group;
    private PropertyConverter converter;

    @Autowired
    ConfigurableEnvironment environment;

    public NacosListener(String group, PropertyConverter converter) {
        this.group = group;
        this.converter = converter;
    }

    @Override
    public void receiveConfigChange(ConfigChangeEvent configChangeEvent) {
        //process encrypt
        //process listener
        //process event
        Map<String, ConfigChange> changeMap = new HashMap<>();
        configChangeEvent.getChangeItems().forEach(c -> {
            String newValue = converter.processValue(c.getNewValue());
            String oldValue = converter.processValue(c.getOldValue());
            ConfigChange configChange = new ConfigChange(group, ConfigUtil.getAppId(), oldValue, newValue, c.getType());
            changeMap.put(c.getKey(), configChange);
            SpringInjector.properties.put(c.getKey(), newValue);
        });
        Config config = ConfigService.getConfig(ConfigUtil.getAppId());
        config.fireConfigChange(group, changeMap);
        com.fxz.fuled.config.starter.model.ConfigChangeEvent event = new com.fxz.fuled.config.starter.model.ConfigChangeEvent(group, changeMap);
        ApplicationContextUtil.getConfigurableApplicationContext().publishEvent(event);
    }
}
