package com.fxz.fuled.config.starter.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class NacosConfigManager {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigManager.class);

    private static ConfigService service = null;

    private NacosConfigProperties nacosConfigProperties;

    public NacosConfigManager(NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
        // Compatible with older code in NacosConfigProperties,It will be deleted in the
        // future.
        createConfigService(nacosConfigProperties);
    }

    /**
     * Compatible with old design,It will be perfected in the future.
     */
    static ConfigService createConfigService(
            NacosConfigProperties nacosConfigProperties) {
        if (Objects.isNull(service)) {
            synchronized (NacosConfigManager.class) {
                try {
                    if (Objects.isNull(service)) {
                        service = NacosFactory.createConfigService(
                                nacosConfigProperties.assembleConfigServiceProperties());
                    }
                }
                catch (NacosException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        return service;
    }

    public ConfigService getConfigService() {
        if (Objects.isNull(service)) {
            createConfigService(this.nacosConfigProperties);
        }
        return service;
    }

    public NacosConfigProperties getNacosConfigProperties() {
        return nacosConfigProperties;
    }

}
