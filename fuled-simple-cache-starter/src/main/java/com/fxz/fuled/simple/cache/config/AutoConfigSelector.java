package com.fxz.fuled.simple.cache.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author fxz
 */
public class AutoConfigSelector implements ImportSelector {
    private String CLASSNAME = "com.fxz.fuled.simple.cache.CacheAspect";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{CLASSNAME};
    }
}
