package com.fxz.fuled.dynamic.mybatis.starter.config;


import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.context.annotation.Bean;

public class AutoConfig {


    @Bean("dynamicMybatisVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-dynamic-mybatis.version", "1.0.0.waterdrop", "fuled-dynamic-mybatis-component");
    }
}
