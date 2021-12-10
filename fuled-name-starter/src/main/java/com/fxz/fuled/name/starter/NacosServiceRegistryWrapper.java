package com.fxz.fuled.name.starter;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
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

    public NacosServiceRegistryWrapper(NacosDiscoveryProperties nacosDiscoveryProperties) {
        super(nacosDiscoveryProperties);
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
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

    private Instance getNacosInstanceFromRegistration(Registration registration) {
        Instance instance = new Instance();
        instance.setIp(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setWeight(nacosDiscoveryProperties.getWeight());
        instance.setClusterName(nacosDiscoveryProperties.getClusterName());
        instance.setEnabled(nacosDiscoveryProperties.isInstanceEnabled());
        Map<String, String> meta = new HashMap<String, String>();
        meta.putAll(registration.getMetadata());
        meta.putAll(System.getenv());
        appendVersionInfo(meta);
        instance.setEphemeral(nacosDiscoveryProperties.isEphemeral());
        instance.setMetadata(meta);
        return instance;
    }

    private void appendVersionInfo(Map meta) {
        meta.put("Fuled-Framework-Version", "1.1.0.WaterDrop");
    }


}
