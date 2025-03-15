package com.fxz.fuled.dynamic.datasource.starter.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fxz.fuled.common.converter.StringValueConveter;
import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.dynamic.datasource.starter.convert.DefaultStringValueConverter;
import com.fxz.fuled.dynamic.datasource.starter.encrypt.EncryptColumn;
import com.fxz.fuled.dynamic.datasource.starter.handler.EncryptColumnHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class DynamicDataSourceAutoConfig implements SmartInitializingSingleton {


    @Autowired(required = false)
    @Lazy
    private List<SqlSessionFactory> sessionFactoryList;

    @Bean("dynamicDatasourceVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-dynamic-datasource.version", "1.0.0.waterdrop", "fuled-dynamic-datasource-component");
    }

    @Bean
    @ConditionalOnMissingBean
    public StringValueConveter defaultStringValueConverter(@Value("${fuled.dynamic.datasource.encrypt.password:}") String password) {
        return new DefaultStringValueConverter(password);
    }

    /**
     * 在多数据源情况下，每个数据源会存在差异，如分页语句，可以通过mybatisplus自行判断dialect
     *
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public MybatisPlusInterceptor innerInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(innerInterceptor);
        return mybatisPlusInterceptor;
    }

    @Override
    public void afterSingletonsInstantiated() {
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
