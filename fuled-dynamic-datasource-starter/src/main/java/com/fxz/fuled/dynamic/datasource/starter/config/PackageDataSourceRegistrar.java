package com.fxz.fuled.dynamic.datasource.starter.config;

import com.fxz.fuled.dynamic.datasource.starter.annotation.DynamicDsConfig;
import com.fxz.fuled.dynamic.datasource.starter.ponitcut.DynamicDSMethodInterceptor;
import com.fxz.fuled.dynamic.datasource.starter.ponitcut.PackagePrefixPointcut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class PackageDataSourceRegistrar implements ImportBeanDefinitionRegistrar {


    private static final String BEAN_NAME_SUFFIX = "%sDynamicDSPointcutAdvisor";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(DynamicDsConfig.class.getName()));
        AnnotationAttributes[] annotationAttributes = (AnnotationAttributes[]) attributes.get("config");
        Class rootClass = (Class) attributes.get("rootClass");
        if (Objects.nonNull(annotationAttributes) && annotationAttributes.length > 0) {
            Map<String, Set<String>> mapped = new HashMap<>();
            for (AnnotationAttributes annotationAttribute : annotationAttributes) {
                String ds = annotationAttribute.getString("ds");
                String[] packages = (String[]) annotationAttribute.get("packages");
                if (StringUtils.isEmpty(ds) || (Objects.isNull(packages) || packages.length == 0)) {
                    log.warn("ds->{} packages->{} skipped", ds, packages);
                } else {
                    Set<String> packSets = mapped.getOrDefault(ds, new HashSet<>());
                    for (String pack : packages) {
                        packSets.add(pack);
                    }
                    mapped.put(ds, packSets);
                }
            }
            if (!CollectionUtils.isEmpty(mapped)) {
                mapped.forEach((k, v) -> {
                    if (!CollectionUtils.isEmpty(v)) {
                        String beanName = String.format(BEAN_NAME_SUFFIX, k);
                        HashMap<String, Object> extraPropertyValues = new HashMap<>();
                        extraPropertyValues.put("advice", new DynamicDSMethodInterceptor(k));
                        PackagePrefixPointcut packagePrefixPointcut = new PackagePrefixPointcut(v, rootClass);
                        extraPropertyValues.put("pointcut", packagePrefixPointcut);
                        registerBeanDefinitionIfNotExists(registry, beanName, DefaultPointcutAdvisor.class, extraPropertyValues);
                    }
                });
            }
        }

    }


    /**
     * @param registry
     * @param beanName
     * @param beanClass
     * @param extraPropertyValues
     * @return
     */
    public boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
                                                     Class<?> beanClass, Map<String, Object> extraPropertyValues) {
        if (registry.containsBeanDefinition(beanName)) {
            return false;
        }
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        if (extraPropertyValues != null) {
            for (Map.Entry<String, Object> entry : extraPropertyValues.entrySet()) {
                beanDefinition.getPropertyValues().add(entry.getKey(), entry.getValue());
            }
        }
        registry.registerBeanDefinition(beanName, beanDefinition);
        return true;
    }
}

