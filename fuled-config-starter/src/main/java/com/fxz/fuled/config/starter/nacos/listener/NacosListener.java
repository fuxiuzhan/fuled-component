package com.fxz.fuled.config.starter.nacos.listener;

import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;
import com.fxz.fuled.common.utils.ConfigUtil;
import com.fxz.fuled.config.starter.Config;
import com.fxz.fuled.config.starter.ConfigService;
import com.fxz.fuled.config.starter.model.ConfigChange;
import com.fxz.fuled.config.starter.nacos.property.NacosPropertySourceRepository;
import com.fxz.fuled.config.starter.spring.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author fuled
 * <p>
 * 每个groupId+dataId都是注册一个listener
 */
public class NacosListener extends AbstractConfigChangeListener {
    private String group;
    private String dataId;
    private static final Logger log = LoggerFactory.getLogger(NacosListener.class);

    public NacosListener(String group, String dataId) {
        this.group = group;
        this.dataId = dataId;
    }

    @Override
    public void receiveConfigChange(ConfigChangeEvent configChangeEvent) {
        //process encrypt
        //process listener
        //process event
        log.info("config changes ->{}", configChangeEvent.getChangeItems().toArray());
//        ValueConverter converter = ApplicationContextUtil.getConfigurableApplicationContext().getBean(ValueConverter.class);
        /**
         * 逐条检查并更新变更的值
         */
        Map<String, ConfigChange> changeMap = new HashMap<>();
        Set<String> changeSet = new HashSet<>();
        configChangeEvent.getChangeItems().forEach(c -> {
            String newValue = c.getNewValue();
//            if (converter != null) {
//                //进行数据转换，有可能是需要解密的数据，其实也可以不解析直接写入，容器获取的时候是会进行一次转换
//                //所以可以properties中的数据是密文，获取的时候再解密
//                newValue = converter.convert(c.getNewValue());
//                log.info("ValueConverter not null processed Result:key->{},oldValue->{},newValue->{}", c.getKey(), c.getNewValue(), newValue);
//            }
            String oldValue = c.getOldValue();
            ConfigChange configChange = new ConfigChange(group, ConfigUtil.getAppId(), oldValue, newValue, c.getType());
            changeMap.put(c.getKey(), configChange);
            log.info("config changes key->{}, newValue->{},oldValue->{}", c.getKey(), c.getNewValue(), c.getOldValue());
            //移除SpringInjector.envMap，使用精确更新具体的groupId和dataId的方式
            //SpringInjector.envMap.put(c.getKey(), newValue);
            NacosPropertySourceRepository.updateExistsValue(group, dataId, c.getKey(), newValue, c.getType());
            changeSet.add(c.getKey());
        });
        Config config = ConfigService.getConfig(ConfigUtil.getAppId());
        /**
         * 发出环境变更事件，重新绑定非单例bean，如@ConfigurationProperties的bean和其他工厂bean
         * 不适用refreshEvent，操作过于笨重
         */
        ApplicationContextUtil.getConfigurableApplicationContext().publishEvent(new EnvironmentChangeEvent(changeSet));

        /**
         * 处理listener
         */
        config.fireConfigChange(ConfigUtil.getAppId(), changeMap);
        /**
         * 发出变更事件
         * 处理$value注解的单例bean
         */
        com.fxz.fuled.config.starter.model.ConfigChangeEvent event = new com.fxz.fuled.config.starter.model.ConfigChangeEvent(group, changeMap);
        ApplicationContextUtil.getConfigurableApplicationContext().publishEvent(event);
    }
}
