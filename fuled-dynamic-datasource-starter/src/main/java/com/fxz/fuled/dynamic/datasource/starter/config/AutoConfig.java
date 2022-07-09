package com.fxz.fuled.dynamic.datasource.starter.config;


import com.fxz.fuled.common.converter.StringValueConveter;
import com.fxz.fuled.common.converter.ValueConverter;
import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.dynamic.datasource.starter.convert.DefaultStringValueConverter;
import com.fxz.fuled.dynamic.datasource.starter.encrypt.EncryptColumn;
import com.fxz.fuled.dynamic.datasource.starter.handler.EncryptColumnHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class AutoConfig implements InitializingBean {

    @Autowired(required = false)
    private List<SqlSessionFactory> sessionFactoryList;

    @Autowired(required = false)
    private ValueConverter valueConverter;

    @Bean("dynamicDatasourceVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-dynamic-datasource.version", "1.0.0.waterdrop", "fuled-dynamic-datasource-component");
    }

    @Bean
    @ConditionalOnMissingBean
    public StringValueConveter defaultStringValueConverter() {
        return new DefaultStringValueConverter();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(sessionFactoryList) && Objects.nonNull(valueConverter)) {
            sessionFactoryList.forEach(f -> f.getConfiguration().getTypeHandlerRegistry().register(EncryptColumn.class, EncryptColumnHandler.class));
        }
    }
}
