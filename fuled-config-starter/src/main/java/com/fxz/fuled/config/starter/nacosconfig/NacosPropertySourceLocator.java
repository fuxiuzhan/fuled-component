package com.fxz.fuled.config.starter.nacosconfig;


import com.alibaba.nacos.api.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Order(0)
public class NacosPropertySourceLocator implements PropertySourceLocator {

    private static final Logger log = LoggerFactory
            .getLogger(NacosPropertySourceLocator.class);

    private static final String NACOS_PROPERTY_SOURCE_NAME = "NACOS";

    private static final String SEP1 = "-";

    private static final String DOT = ".";

    private NacosPropertySourceBuilder nacosPropertySourceBuilder;

    private NacosConfigProperties nacosConfigProperties;

    private NacosConfigManager nacosConfigManager;


    @Deprecated
    public NacosPropertySourceLocator(NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
    }

    public NacosPropertySourceLocator(NacosConfigManager nacosConfigManager) {
        this.nacosConfigManager = nacosConfigManager;
        this.nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();
    }

    @Override
    public PropertySource<?> locate(Environment env) {
        nacosConfigProperties.setEnvironment(env);
        ConfigService configService = nacosConfigManager.getConfigService();

        if (null == configService) {
            log.warn("no instance of config service found, can't load config from nacos");
            return null;
        }
        long timeout = nacosConfigProperties.getTimeout();
        nacosPropertySourceBuilder = new NacosPropertySourceBuilder(configService,
                timeout);
        String name = nacosConfigProperties.getName();

        String dataIdPrefix = nacosConfigProperties.getPrefix();
        if (StringUtils.isEmpty(dataIdPrefix)) {
            dataIdPrefix = name;
        }

        if (StringUtils.isEmpty(dataIdPrefix)) {
            dataIdPrefix = env.getProperty("spring.application.name");
        }

        CompositePropertySource composite = new CompositePropertySource(
                NACOS_PROPERTY_SOURCE_NAME);

        loadSharedConfiguration(composite);
        loadExtConfiguration(composite);
        loadApplicationConfiguration(composite, dataIdPrefix, nacosConfigProperties, env);
        return composite;
    }

    /**
     * load shared configuration.
     */
    private void loadSharedConfiguration(
            CompositePropertySource compositePropertySource) {
        List<NacosConfigProperties.Config> sharedConfigs = nacosConfigProperties
                .getSharedConfigs();
        if (!CollectionUtils.isEmpty(sharedConfigs)) {
            checkConfiguration(sharedConfigs, "shared-configs");
            loadNacosConfiguration(compositePropertySource, sharedConfigs);
        }
    }

    /**
     * load extensional configuration.
     */
    private void loadExtConfiguration(CompositePropertySource compositePropertySource) {
        List<NacosConfigProperties.Config> extConfigs = nacosConfigProperties
                .getExtensionConfigs();
        if (!CollectionUtils.isEmpty(extConfigs)) {
            checkConfiguration(extConfigs, "extension-configs");
            loadNacosConfiguration(compositePropertySource, extConfigs);
        }
    }

    /**
     * load configuration of application.
     */
    private void loadApplicationConfiguration(
            CompositePropertySource compositePropertySource, String dataIdPrefix,
            NacosConfigProperties properties, Environment environment) {
        String fileExtension = properties.getFileExtension();
        String nacosGroup = properties.getGroup();
        // load directly once by default
        loadNacosDataIfPresent(compositePropertySource, dataIdPrefix, nacosGroup,
                fileExtension, true);
        // load with suffix, which have a higher priority than the default
        loadNacosDataIfPresent(compositePropertySource,
                dataIdPrefix + DOT + fileExtension, nacosGroup, fileExtension, true);
        // Loaded with profile, which have a higher priority than the suffix
        for (String profile : environment.getActiveProfiles()) {
            String dataId = dataIdPrefix + SEP1 + profile + DOT + fileExtension;
            loadNacosDataIfPresent(compositePropertySource, dataId, nacosGroup,
                    fileExtension, true);
        }

    }

    private void loadNacosConfiguration(final CompositePropertySource composite,
                                        List<NacosConfigProperties.Config> configs) {
        for (NacosConfigProperties.Config config : configs) {
            loadNacosDataIfPresent(composite, config.getDataId(), config.getGroup(),
                    NacosDataParserHandler.getInstance()
                            .getFileExtension(config.getDataId()),
                    config.isRefresh());
        }
    }

    private void checkConfiguration(List<NacosConfigProperties.Config> configs,
                                    String tips) {
        for (int i = 0; i < configs.size(); i++) {
            String dataId = configs.get(i).getDataId();
            if (dataId == null || dataId.trim().length() == 0) {
                throw new IllegalStateException(String.format(
                        "the [ spring.cloud.nacos.config.%s[%s] ] must give a dataId",
                        tips, i));
            }
        }
    }

    private void loadNacosDataIfPresent(final CompositePropertySource composite,
                                        final String dataId, final String group, String fileExtension,
                                        boolean isRefreshable) {
        if (null == dataId || dataId.trim().length() < 1) {
            return;
        }
        if (null == group || group.trim().length() < 1) {
            return;
        }
        NacosPropertySource propertySource = this.loadNacosPropertySource(dataId, group,
                fileExtension, isRefreshable);
        this.addFirstPropertySource(composite, propertySource, false);
    }

    private NacosPropertySource loadNacosPropertySource(final String dataId,
                                                        final String group, String fileExtension, boolean isRefreshable) {
        return nacosPropertySourceBuilder.build(dataId, group, fileExtension,
                isRefreshable);
    }

    /**
     * Add the nacos configuration to the first place and maybe ignore the empty
     * configuration.
     */
    private void addFirstPropertySource(final CompositePropertySource composite,
                                        NacosPropertySource nacosPropertySource, boolean ignoreEmpty) {
        if (null == nacosPropertySource || null == composite) {
            return;
        }
        if (ignoreEmpty && nacosPropertySource.getSource().isEmpty()) {
            return;
        }
        composite.addFirstPropertySource(nacosPropertySource);
    }

    public void setNacosConfigManager(NacosConfigManager nacosConfigManager) {
        this.nacosConfigManager = nacosConfigManager;
    }

}
