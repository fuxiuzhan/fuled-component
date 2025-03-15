package com.fxz.fuled.dynamic.datasource.starter.annotation;

import com.fxz.fuled.dynamic.datasource.starter.config.PackageDataSourceRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(PackageDataSourceRegistrar.class)
public @interface DynamicDsConfig {

    /**
     * package ds config
     *
     * @return
     */
    PackageConfig[] config() default {};

    /**
     * BaseClass ,for example Proxy.class only Mapper be proxy
     *
     * @return
     */
    Class<?> rootClass() default Object.class;

    //
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface PackageConfig {

        /**
         * packages
         *
         * @return
         */
        String[] packages() default {};

        /**
         * datasource
         *
         * @return
         */
        String ds() default "";
    }
}
