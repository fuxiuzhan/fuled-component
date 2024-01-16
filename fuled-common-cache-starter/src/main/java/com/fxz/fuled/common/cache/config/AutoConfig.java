package com.fxz.fuled.common.cache.config;

import com.fxz.fuled.common.cache.aspect.CacheAspect;
import com.fxz.fuled.common.cache.container.CacheContainer;
import com.fxz.fuled.common.cache.container.LruCacheContainer;
import com.fxz.fuled.common.cache.filters.LocalCacheFilter;
import com.fxz.fuled.common.cache.filters.ProceedCacheFilter;
import com.fxz.fuled.common.cache.filters.abs.PrepareCacheFilter;
import com.fxz.fuled.common.cache.resolver.DefaultKeyResolver;
import com.fxz.fuled.common.cache.resolver.KeyResolver;
import com.fxz.fuled.common.chain.FilterChainManger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import({CacheAspect.class, FilterChainManger.class})
public class AutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public CacheContainer defaultCacheContainer(@Value("${fuled.common.cache.lru.max.size:4096}") Integer maxSize) {
        return new LruCacheContainer(maxSize);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeyResolver keyResolver() {
        return new DefaultKeyResolver();
    }


    @Bean
    @ConditionalOnMissingBean
    public LocalCacheFilter localCacheFilter(CacheContainer cacheContainer) {
        return new LocalCacheFilter(cacheContainer);
    }

    @Bean
    public PrepareCacheFilter prepareCacheFilter(KeyResolver keyResolver) {
        return new PrepareCacheFilter(keyResolver);
    }

    @Bean
    public ProceedCacheFilter proceedCacheFilter() {
        return new ProceedCacheFilter();
    }
}
