package com.fxz.component.fuled.cat.starter.configuration;

import com.fxz.component.fuled.cat.starter.component.mysql.CatMybatisInterceptor;
import com.fxz.component.fuled.cat.starter.mark.Mark;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fuled
 */
@Configuration
@ConditionalOnClass({MybatisProperties.class})
@ConditionalOnBean(Mark.MarkClass.class)
public class CatMybatisConfiguration {
    @Bean
    public CatMybatisInterceptor catMybatisInterceptor() {
        return new CatMybatisInterceptor();
    }
}
