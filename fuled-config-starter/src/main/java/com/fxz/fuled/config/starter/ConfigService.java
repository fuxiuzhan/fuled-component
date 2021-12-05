/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.fxz.fuled.config.starter;


import com.fxz.fuled.config.starter.nacos.DefaultConfig;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigService {


    private static ConcurrentHashMap<String, Config> configConcurrentHashMap = new ConcurrentHashMap<>();

    /**
     * Get the config instance for the namespace.
     *
     * @param namespace the namespace of the config
     * @return config instance
     */
    public synchronized static Config getConfig(String namespace) {
        if (configConcurrentHashMap.containsKey(namespace)) {
            return configConcurrentHashMap.get(namespace);
        } else {
            DefaultConfig config = new DefaultConfig(namespace);
            configConcurrentHashMap.put(namespace, config);
            return config;
        }
    }
}
