package com.fxz.fuled.name.starter;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fxz.fuled.common.version.ComponentVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.ReflectionUtils.rethrowRuntimeException;

/**
 * @author fxz
 */
public class NacosServiceRegistryWrapper extends NacosServiceRegistry {

    @Autowired
    private NacosServiceManager nacosServiceManager;
    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    private static final Logger log = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private List<ComponentVersion> componentVersions;

    public NacosServiceRegistryWrapper(NacosDiscoveryProperties nacosDiscoveryProperties, List<ComponentVersion> componentVersions) {
        super(nacosDiscoveryProperties);
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        this.componentVersions = componentVersions;
    }


    @Override
    public void register(Registration registration) {

        if (StringUtils.isEmpty(registration.getServiceId())) {
            log.warn("No service to register for nacos client...");
            return;
        }

        NamingService namingService = namingService();
        String serviceId = registration.getServiceId();
        String group = nacosDiscoveryProperties.getGroup();

        Instance instance = getNacosInstanceFromRegistration(registration);

        try {
            namingService.registerInstance(serviceId, group, instance);
            log.info("nacos registry, {} {} {}:{} register finished", group, serviceId,
                    instance.getIp(), instance.getPort());
        } catch (Exception e) {
            if (nacosDiscoveryProperties.isFailFast()) {
                log.error("nacos registry, {} register failed...{},", serviceId,
                        registration.toString(), e);
                rethrowRuntimeException(e);
            } else {
                log.warn("Failfast is false. {} register failed...{},", serviceId,
                        registration.toString(), e);
            }
        }
    }


    private NamingService namingService() {
        return nacosServiceManager
                .getNamingService(nacosDiscoveryProperties.getNacosProperties());
    }

    /**
     * 增加其他meta信息，这些信息可以作为应用管理的数据，如统计应用及版本信息，自动更新提醒等
     * 也可以作为负载均衡的参数，如果使用feign方式，直接重写IRule来自定义负载策略
     *
     * @param registration
     * @return
     */
    private Instance getNacosInstanceFromRegistration(Registration registration) {
        Instance instance = new Instance();
        instance.setIp(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setWeight(nacosDiscoveryProperties.getWeight());
        instance.setClusterName(nacosDiscoveryProperties.getClusterName());
        instance.setEnabled(nacosDiscoveryProperties.isInstanceEnabled());
        Map<String, String> meta = new LinkedHashMap<>();
        meta.putAll(registration.getMetadata());
//        meta.putAll(System.getenv());
        appendVersionInfo(meta);
        instance.setEphemeral(nacosDiscoveryProperties.isEphemeral());
        instance.setMetadata(meta);
        return instance;
    }

    private void appendVersionInfo(Map meta) {
        if (!CollectionUtils.isEmpty(componentVersions)) {
            componentVersions.forEach(c -> {
                meta.put(c.getName(), c.getVersion() + "," + c.getDesc());
            });
        }
        meta.put("env", System.getProperties().get("env"));
        meta.put("os.name", System.getProperties().get("os.name"));
        meta.put("os.version", System.getProperties().get("os.version"));
        meta.put("cpus", Runtime.getRuntime().availableProcessors());
        meta.put("OS", System.getenv("OS"));
        meta.put("COMPUTERNAME", System.getenv("COMPUTERNAME"));
        meta.put("user.name", System.getProperties().get("user.name"));
        meta.put("PID", System.getProperties().get("PID"));
        meta.put("user.home", System.getProperties().get("user.home"));
        meta.put("os.arch", System.getProperties().get("os.arch"));
        meta.put("@appId", System.getProperties().get("@appId"));
        meta.put("java.runtime.version", System.getProperties().get("java.runtime.version"));
        meta.put("java.version", System.getProperties().get("java.version"));
        meta.put("spring.application.name", System.getProperties().get("spring.application.name"));
        meta.put("java.vm.name", System.getProperties().get("java.vm.name"));
        meta.put("line.separator", System.getProperties().get("line.separator"));
        meta.put("user.timezone", System.getProperties().get("user.timezone"));

    }
}
