package com.fxz.fuled.config.starter.nacos.property;


import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fxz.fuled.config.starter.nacos.NacosDataParserHandler;
import com.fxz.fuled.config.starter.nacos.listener.NacosListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NacosPropertySourceBuilder {

    private static final Logger log = LoggerFactory
            .getLogger(NacosPropertySourceBuilder.class);

    private ConfigService configService;

    private long timeout;

    public NacosPropertySourceBuilder(ConfigService configService, long timeout) {
        this.configService = configService;
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * @param dataId Nacos dataId
     * @param group  Nacos group
     */
    NacosPropertySource build(String dataId, String group, String fileExtension,
                              boolean isRefreshable) {
        List<PropertySource<?>> propertySources = loadNacosData(dataId, group,
                fileExtension);
        NacosPropertySource nacosPropertySource = new NacosPropertySource(propertySources,
                group, dataId, new Date(), isRefreshable);
        NacosPropertySourceRepository.collectNacosPropertySource(nacosPropertySource);
        return nacosPropertySource;
    }

    private List<PropertySource<?>> loadNacosData(String dataId, String group,
                                                  String fileExtension) {
        String data = null;
        try {
            data = configService.getConfig(dataId, group, timeout);
            log.info("get config from nacos server : dataId->{},groupId->{},result->{}", dataId, group, data);
            configService.addListener(dataId, group, new NacosListener(group));
            if (StringUtils.isEmpty(data)) {
                log.warn(
                        "Ignore the empty nacos configuration and get it based on dataId[{}] & group[{}]",
                        dataId, group);
                return Collections.emptyList();
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format(
                        "Loading nacos data, dataId: '%s', group: '%s', data: %s", dataId,
                        group, data));
            }
            return NacosDataParserHandler.getInstance().parseNacosData(dataId, data,
                    fileExtension);
        } catch (NacosException e) {
            log.error("get data from Nacos error,dataId:{} ", dataId, e);
        } catch (Exception e) {
            log.error("parse data from Nacos error,dataId:{},data:{}", dataId, data, e);
        }
        return Collections.emptyList();
    }

}
