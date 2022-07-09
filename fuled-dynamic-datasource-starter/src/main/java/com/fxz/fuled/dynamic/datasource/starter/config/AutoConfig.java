package com.fxz.fuled.dynamic.datasource.starter.config;


import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.context.annotation.Bean;

public class AutoConfig {


    @Bean("dynamicDatasourceVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-dynamic-datasource.version", "1.0.0.waterdrop", "fuled-dynamic-datasource-component");
    }
}
