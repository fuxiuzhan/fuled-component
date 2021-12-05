package com.fxz.fuled.config.starter.spring.property;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.common.utils.StringUtils;
import com.fxz.fuled.config.starter.enums.Env;
import com.fxz.fuled.config.starter.nacos.DefaultPropertyConverter;
import com.fxz.fuled.config.starter.nacos.NacosConfigProperties;
import com.fxz.fuled.config.starter.nacos.PropertyConverter;
import com.fxz.fuled.config.starter.spring.util.ConfigUtil;
import com.fxz.fuled.config.starter.spring.util.SpringInjector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author fxz
 */
public class PropertiesInjector implements EnvironmentPostProcessor {
    private Log logger = LogFactory.getLog(PropertiesInjector.class);
    PropertyConverter propertyConverter = new DefaultPropertyConverter();
    ConfigurableEnvironment environment;

    private void init() {
        Properties properties = new Properties();
        SpringInjector.properties = properties;
        try {
            ConfigUtil.initialize();
            Env env = ConfigUtil.getEnv();
            ConfigService configService = NacosFactory.createConfigService(env.getConfigServer() + ":" + env.getPort());
            String config = configService.getConfig(ConfigUtil.getAppId(), NacosConfigProperties.DEFAULT_GROUP, 10000L);
            if (StringUtils.isNotEmpty(config)) {
                List<String> kv = Arrays.asList(config.split(System.lineSeparator()));
                kv.forEach(l -> {
                    String[] split = l.split("=");
                    properties.put(propertyConverter.processValue(split[0]), propertyConverter.processValue(split[1]));
                });
            }
        } catch (Exception e) {
            logger.error(e);
        }
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("nacos-application.properties", properties);
        environment.getPropertySources().addFirst(propertiesPropertySource);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        this.environment = (ConfigurableEnvironment) environment;
        init();
    }
}
