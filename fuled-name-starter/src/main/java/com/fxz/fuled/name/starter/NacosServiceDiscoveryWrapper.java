package com.fxz.fuled.name.starter;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fxz.fuled.common.utils.ConfigUtil;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fxz
 */
public class NacosServiceDiscoveryWrapper extends NacosServiceDiscovery {

    private NacosDiscoveryProperties discoveryProperties;

    private NacosServiceManager nacosServiceManager;

    public NacosServiceDiscoveryWrapper(NacosDiscoveryProperties discoveryProperties, NacosServiceManager nacosServiceManager) {
        //rewrite env
        super(discoveryProperties, nacosServiceManager);
        initEnv(discoveryProperties);
        this.discoveryProperties = discoveryProperties;
        this.nacosServiceManager = nacosServiceManager;
    }

    private void initEnv(NacosDiscoveryProperties discoveryProperties) {
        ConfigUtil.initialize();
        discoveryProperties.setServerAddr(ConfigUtil.getEnv().getConfigServer() + ":" + ConfigUtil.getEnv().getPort());
//        discoveryProperties.setGroup(ConfigUtil.getAppId().toUpperCase());
        discoveryProperties.setNamespace(ConfigUtil.getEnv().name().toUpperCase());
        discoveryProperties.setService(ConfigUtil.getAppId());
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) throws NacosException {
        String group = discoveryProperties.getGroup();
        List<Instance> instances = namingService().selectInstances(serviceId, group,
                true);
        return hostToServiceInstanceList(instances, serviceId);
    }

    public static List<ServiceInstance> hostToServiceInstanceList(
            List<Instance> instances, String serviceId) {
        List<ServiceInstance> result = new ArrayList<ServiceInstance>(instances.size());
        for (Instance instance : instances) {
            ServiceInstance serviceInstance = hostToServiceInstance(instance, serviceId);
            if (serviceInstance != null) {
                result.add(serviceInstance);
            }
        }
        return result;
    }

    public static ServiceInstance hostToServiceInstance(Instance instance,
                                                        String serviceId) {
        if (instance == null || !instance.isEnabled() || !instance.isHealthy()) {
            return null;
        }
        NacosServiceInstance nacosServiceInstance = new NacosServiceInstance();
        nacosServiceInstance.setHost(instance.getIp());
        nacosServiceInstance.setPort(instance.getPort());
        nacosServiceInstance.setServiceId(serviceId);

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("nacos.instanceId", instance.getInstanceId());
        metadata.put("nacos.weight", instance.getWeight() + "");
        metadata.put("nacos.healthy", instance.isHealthy() + "");
        metadata.put("nacos.cluster", instance.getClusterName() + "");
        if (instance.getMetadata() != null) {
            metadata.putAll(instance.getMetadata());
        }
        metadata.put("nacos.ephemeral", String.valueOf(instance.isEphemeral()));
        nacosServiceInstance.setMetadata(metadata);
        if (metadata.containsKey("secure")) {
            boolean secure = Boolean.parseBoolean(metadata.get("secure"));
            nacosServiceInstance.setSecure(secure);
        }
        return nacosServiceInstance;
    }

    private NamingService namingService() {
        return nacosServiceManager
                .getNamingService(discoveryProperties.getNacosProperties());
    }
}
