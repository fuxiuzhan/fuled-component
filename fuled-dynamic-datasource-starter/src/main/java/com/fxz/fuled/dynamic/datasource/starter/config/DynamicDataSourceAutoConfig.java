package com.fxz.fuled.dynamic.datasource.starter.config;


import com.fxz.fuled.common.converter.StringValueConveter;
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

public class DynamicDataSourceAutoConfig implements InitializingBean {

    @Autowired(required = false)
    private List<SqlSessionFactory> sessionFactoryList;

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
    public void afterPropertiesSet() {
        if (!CollectionUtils.isEmpty(sessionFactoryList)) {
            //WARN
            // baomidou的dynamic datasource 只会将primary 的datasource 的sqlSession注入容器
            //其他的dataSource都是通过内部的map自行管理，不过并不影响，sqlSession是高于dataSource一层的逻辑
            //dataSource是DynamicRoutingDataSource，mybatis执行语句的时候会通过dataSource getConnection
            //获取新的链接，然后通过dataSource的标记来选择特定的数据源
            sessionFactoryList.forEach(f -> f.getConfiguration().getTypeHandlerRegistry().register(EncryptColumn.class, EncryptColumnHandler.class));
        }
    }
}
