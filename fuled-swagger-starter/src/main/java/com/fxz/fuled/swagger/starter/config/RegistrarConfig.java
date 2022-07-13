package com.fxz.fuled.swagger.starter.config;

import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Component
@EnableSwagger2
public class RegistrarConfig implements ImportBeanDefinitionRegistrar {
    @Value(("${fuled.app.swagger.url:}"))
    private String url;
    @Value(("${fuled.app.swagger.mail:}"))
    private String mail;
    @Value("${fuled.app.swagger.version:1.0.0}")
    private String version;
    @Value("${fuled.app.swagger.desc:appDesc}")
    private String desc;
    @Value("${fuled.app.swagger.title:}")
    private String title;
    private static AnnotationMetadata importingClassMetadata;

    @Bean
    public Docket createRestApi(ApplicationContext applicationContext) {
        String appName = applicationContext.getEnvironment().getProperty("spring.application.name", "");
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage(ClassUtils.getPackageName(importingClassMetadata.getClassName())))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .title(StringUtils.isEmpty(title) ? appName : title)
                        .description(desc)
                        .version(version)
                        .contact(new Contact(appName, url, mail))
                        .license("")
                        .licenseUrl(url)
                        .build());
    }

    @Bean("swaggerVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-swagger.version", "1.0.0.waterdrop", "fuled-swagger-component");
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RegistrarConfig.importingClassMetadata = importingClassMetadata;
    }
}
