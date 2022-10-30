package com.fxz.componet.plugin.dynamic.threadpool.dubbo;

import com.fxz.fuled.common.utils.ReflectionUtil;
import com.fxz.fuled.dynamic.threadpool.ThreadPoolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.store.DataStore;
import org.apache.dubbo.common.threadpool.manager.DefaultExecutorRepository;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 获取dubbo线程池并注册
 */
@Slf4j
public class DubboExecutorsWrapper implements SmartInitializingSingleton {

    private static final String EXECUTOR_SERVICE_COMPONENT_KEY = ExecutorService.class.getName();

    public static final String CONSUMER = "consumer";

    public static final String PROVIDER = "provider";

    @Override
    public void afterSingletonsInstantiated() {
        String currVersion = Version.getVersion();
        if (DubboVersion.compare(DubboVersion.VERSION_2_7_5, currVersion) > 0) {
            DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
            if (Objects.isNull(dataStore)) {
                return;
            }
            Map<String, Object> executors = dataStore.get(EXECUTOR_SERVICE_COMPONENT_KEY);
            if (Objects.nonNull(executors) && !executors.isEmpty()) {
                executors.forEach((k, v) -> ThreadPoolRegistry.registerThreadPool(k, (ThreadPoolExecutor) v));
            }
            return;
        }
        ExecutorRepository executorRepository;
        if (DubboVersion.compare(currVersion, DubboVersion.VERSION_3_0_3) >= 0) {
            executorRepository = ApplicationModel.defaultModel().getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
        } else {
            executorRepository = ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
        }
        ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>> data = (ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>>) ReflectionUtil.getFieldValue(DefaultExecutorRepository.class, "data", executorRepository);
        if (Objects.isNull(data)) {
            return;
        }
        Map<Integer, ExecutorService> executorMap = data.get(EXECUTOR_SERVICE_COMPONENT_KEY);
        if (Objects.nonNull(executorMap) && !executorMap.isEmpty()) {
            executorMap.forEach((k, v) -> ThreadPoolRegistry.registerThreadPool("dubbo_" + k, (ThreadPoolExecutor) v));
        }
        Map<Integer, ExecutorService> executorConsumerMap = data.get(CONSUMER);
        if (Objects.nonNull(executorConsumerMap) && !executorConsumerMap.isEmpty()) {
            executorConsumerMap.forEach((k, v) -> ThreadPoolRegistry.registerThreadPool("consumer_" + k, (ThreadPoolExecutor) v));
        }
        Map<Integer, ExecutorService> executorProviderMap = data.get(PROVIDER);
        if (Objects.nonNull(executorProviderMap) && !executorProviderMap.isEmpty()) {
            executorProviderMap.forEach((k, v) -> ThreadPoolRegistry.registerThreadPool("provider_" + k, (ThreadPoolExecutor) v));
        }
    }

}
