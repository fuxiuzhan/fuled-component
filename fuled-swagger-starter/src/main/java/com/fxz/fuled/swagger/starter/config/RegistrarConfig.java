package com.fxz.fuled.swagger.starter.config;

import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.swagger.starter.advice.ControllerExceptionAdvice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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

import java.util.Objects;

@Component
@EnableSwagger2
@ConditionalOnWebApplication
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
    @Value("${fuled.app.swagger.license:}")
    private String license;
    private static AnnotationMetadata importingClassMetadata;
    private static final String defaultPackage = "com.fxz.fuled";

    @Bean
    public Docket createRestApi(ApplicationContext applicationContext) {
        String appName = applicationContext.getEnvironment().getProperty("spring.application.name", "");
        String basePackage = defaultPackage;
        if (Objects.nonNull(importingClassMetadata)) {
            basePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .title(StringUtils.isEmpty(title) ? appName : title)
                        .description(desc)
                        .version(version)
                        .contact(new Contact(appName, url, mail))
                        .license(license)
                        .licenseUrl(url)
                        .build());
    }

    @Bean("swaggerVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-swagger.version", "1.0.0.waterdrop", "fuled-swagger-component");
    }

    @Bean
    public ControllerExceptionAdvice controllerExceptionAdvice() {
        return new ControllerExceptionAdvice();
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RegistrarConfig.importingClassMetadata = importingClassMetadata;
        if (!registry.containsBeanDefinition(RegistrarConfig.class.getName())) {
            registry.registerBeanDefinition(RegistrarConfig.class.getName(), BeanDefinitionBuilder.genericBeanDefinition(RegistrarConfig.class).getBeanDefinition());
        }
    }
}
