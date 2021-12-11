package com.fxz.fuled.config.starter.nacos.listener;

import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;
import com.fxz.fuled.common.ConfigUtil;
import com.fxz.fuled.config.starter.Config;
import com.fxz.fuled.config.starter.ConfigService;
import com.fxz.fuled.config.starter.model.ConfigChange;
import com.fxz.fuled.config.starter.spring.util.ApplicationContextUtil;
import com.fxz.fuled.config.starter.spring.util.SpringInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.endpoint.event.RefreshEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fuled
 */
public class NacosListener extends AbstractConfigChangeListener {
    private String group;
    private static final Logger log = LoggerFactory.getLogger(NacosListener.class);

    public NacosListener(String group) {
        this.group = group;
    }

    @Override
    public void receiveConfigChange(ConfigChangeEvent configChangeEvent) {
        //process encrypt
        //process listener
        //process event
        log.info("config changes ->{}", configChangeEvent.getChangeItems().toArray());
        Map<String, ConfigChange> changeMap = new HashMap<>();
        configChangeEvent.getChangeItems().forEach(c -> {
            String newValue = c.getNewValue();
            String oldValue = c.getOldValue();
            ConfigChange configChange = new ConfigChange(group, ConfigUtil.getAppId(), oldValue, newValue, c.getType());
            changeMap.put(c.getKey(), configChange);
            log.info("config changes key->{}, newValue->{},oldValue->{}", c.getKey(), c.getNewValue(), c.getOldValue());
            SpringInjector.properties.put(c.getKey(), newValue);
        });
        Config config = ConfigService.getConfig(ConfigUtil.getAppId());
        config.fireConfigChange(group, changeMap);
        com.fxz.fuled.config.starter.model.ConfigChangeEvent event = new com.fxz.fuled.config.starter.model.ConfigChangeEvent(group, changeMap);
        ApplicationContextUtil.getConfigurableApplicationContext().publishEvent(event);
        ApplicationContextUtil.getConfigurableApplicationContext().publishEvent(new RefreshEvent(this, null, "refresh properties"));
    }
}
