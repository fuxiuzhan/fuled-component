package com.fxz.fuled.dynamic.redis.config;

import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.dynamic.redis.properties.DynamicProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 处理多redis数据源，
 * 将属性，链接及template注入容器
 *
 * @author fxz
 */

@AutoConfigureBefore(RedisAutoConfiguration.class)
@Slf4j
public class DynamicConfig implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

    private final String stringTemplateSuffix = "StringRedisTemplate";

    private final String templateSuffix = "RedisTemplate";

    private final String redisConnectionFactorySuffix = "RedisConnectionFactory";

    private final String redisPropertiesSuffix = "RedisProperties";

    private final String defaultRedisPropertiesBeanName = "redisProperties";
    private BeanDefinitionRegistry registry;
    private ConfigurableListableBeanFactory beanFactory;
    private Environment environment;


    @Bean
    @ConditionalOnMissingBean
    public ConfigConvert defaultConfigConvert() {
        return new ConfigConvert() {
            @Override
            public DynamicProperties convert(DynamicProperties dynamicProperties) {
                return ConfigConvert.super.convert(dynamicProperties);
            }
        };
    }

    /**
     * 注入
     * 注入四个组件
     * connectionFactory
     * redisProperties
     * stringRedisTemplate
     * redisTemplate
     *
     * @param dynamicProperties
     */
    public synchronized void init(DynamicProperties dynamicProperties) {
        if (!CollectionUtils.isEmpty(dynamicProperties.getConfig())) {
            dynamicProperties.getConfig().forEach((k, v) -> {
                boolean isPrimary = (!StringUtils.isEmpty(dynamicProperties.getMaster())) && dynamicProperties.getMaster().equals(k);
                //默认注入lettuceConnectionFactory
                RedisConnectionFactory lettuceConnectionFactory = createLettuceConnectionFactory(v);
                BeanDefinitionBuilder redisConnectionFactoryBeanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(RedisConnectionFactory.class, () -> lettuceConnectionFactory);
                BeanDefinition redisConnectionFactoryBeanDef = redisConnectionFactoryBeanDefBuilder.getRawBeanDefinition();
                if (isPrimary) {
                    removeBeanDefIfPresent(k + redisConnectionFactorySuffix);
                    redisConnectionFactoryBeanDef.setPrimary(Boolean.TRUE);
                }
                registry.registerBeanDefinition(k + redisConnectionFactorySuffix, redisConnectionFactoryBeanDef);
                //注入stringRedisTemplate
                BeanDefinitionBuilder stringRedisTemplateBeanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(StringRedisTemplate.class, () -> {
                    StringRedisTemplate template = new StringRedisTemplate();
                    template.setConnectionFactory((RedisConnectionFactory) beanFactory.getBean(k + redisConnectionFactorySuffix));
                    return template;
                });
                BeanDefinition stringRedisTemplateBeanDef = stringRedisTemplateBeanDefBuilder.getRawBeanDefinition();
                registry.registerBeanDefinition(k + stringTemplateSuffix, stringRedisTemplateBeanDef);
                //注入redisTemplate
                BeanDefinitionBuilder redisTemplateBeanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(RedisTemplate.class, () -> {
                    RedisTemplate<Object, Object> template = new RedisTemplate<>();
                    template.setConnectionFactory((RedisConnectionFactory) beanFactory.getBean(k + redisConnectionFactorySuffix));
                    return template;
                });
                BeanDefinition redisTemplateBeanDef = redisTemplateBeanDefBuilder.getRawBeanDefinition();
                registry.registerBeanDefinition(k + templateSuffix, redisTemplateBeanDef);
                //注入redisProperties
                BeanDefinitionBuilder redisPropertiesBeanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(RedisProperties.class, () -> v);
                BeanDefinition redisPropertiesBeanDef = redisPropertiesBeanDefBuilder.getRawBeanDefinition();
                registry.registerBeanDefinition(k + redisPropertiesSuffix, redisPropertiesBeanDef);
                if (isPrimary) {
                    BeanDefinition redisPropertiesBeanDefPrimary = BeanDefinitionBuilder.genericBeanDefinition(RedisProperties.class, () -> v).getRawBeanDefinition();
                    redisPropertiesBeanDefPrimary.setPrimary(Boolean.TRUE);
                    replaceBeanDefIfPresent(redisPropertiesBeanDefPrimary, defaultRedisPropertiesBeanName);
                }
            });
        }
    }

    /**
     * @param beanDefinition
     * @param beanName
     */
    private void replaceBeanDefIfPresent(BeanDefinition beanDefinition, String beanName) {
        removeBeanDefIfPresent(beanName);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }


    /**
     * @param beanName
     */
    private void removeBeanDefIfPresent(String beanName) {
        if (registry.containsBeanDefinition(beanName)) {
            registry.removeBeanDefinition(beanName);
        }
    }


    private GenericObjectPoolConfig createPoolConfig(RedisProperties redisProperties) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        if (Objects.nonNull(redisProperties.getJedis()) && Objects.nonNull(redisProperties.getJedis().getPool())) {
            RedisProperties.Pool pool = redisProperties.getJedis().getPool();
            applyConfig(genericObjectPoolConfig, pool);
            return genericObjectPoolConfig;
        }
        if (Objects.nonNull(redisProperties.getLettuce()) && Objects.nonNull(redisProperties.getLettuce().getPool())) {
            RedisProperties.Pool pool = redisProperties.getLettuce().getPool();
            applyConfig(genericObjectPoolConfig, pool);
            return genericObjectPoolConfig;
        }
        return genericObjectPoolConfig;
    }

    public void applyConfig(GenericObjectPoolConfig genericObjectPoolConfig, RedisProperties.Pool pool) {
        genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
        genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
    }

    private RedisConnectionFactory createLettuceConnectionFactory(RedisProperties redisProperties) {
        GenericObjectPoolConfig genericObjectPoolConfig = createPoolConfig(redisProperties);
        RedisConfiguration redisConfiguration;
        //Cluster
        if (Objects.nonNull(redisProperties.getCluster()) && CollectionUtils.isEmpty(redisProperties.getCluster().getNodes())) {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            List<RedisNode> redisNodeList = new ArrayList<>();
            for (String singleNode : redisProperties.getCluster().getNodes()) {
                if (!StringUtils.isEmpty(singleNode) && singleNode.contains(":")) {
                    redisNodeList.add(new RedisNode(singleNode.split(":")[0], Integer.parseInt(singleNode.split(":")[1])));
                }
            }
            redisClusterConfiguration.setClusterNodes(redisNodeList);
            redisClusterConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
            redisConfiguration = redisClusterConfiguration;
        } else {
            //Standalone
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
            redisStandaloneConfiguration.setHostName(redisProperties.getHost());
            redisStandaloneConfiguration.setPort(redisProperties.getPort());
            redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
            redisConfiguration = redisStandaloneConfiguration;
        }
        Duration duration = redisProperties.getTimeout();
        if (Objects.isNull(duration)) {
            duration = Duration.ofMillis(5000);
        }
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder().commandTimeout(duration).shutdownTimeout(Duration.ofMillis(1000)).poolConfig(genericObjectPoolConfig).build();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration, clientConfig);
        return factory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
        DynamicProperties dynamicProperties = new DynamicProperties();
        Binder.get(environment).bind(DynamicProperties.PREFIX, Bindable.ofInstance(dynamicProperties));
        try {
            ConfigConvert bean = beanFactory.getBean(ConfigConvert.class);
            dynamicProperties = bean.convert(dynamicProperties);
        } catch (Exception e) {
            log.warn("dynamicProperties convert not found using default");
        }
        init(dynamicProperties);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean("dynamicRedisVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-dynamic-redis.version", "1.0.0.waterdrop", "fuled-dynamic-redis-component");
    }
}
