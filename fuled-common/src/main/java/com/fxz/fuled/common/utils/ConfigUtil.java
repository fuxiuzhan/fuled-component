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
package com.fxz.fuled.common.utils;


import com.fxz.fuled.common.Env;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason Song(song_s@ctrip.com)
 */

public class ConfigUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    public static final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";
    public static final String APP_ID = "app.id";
    public static final String APP_ID_ENVIRONMENT_VARIABLES = "APP.ID";
    private static Properties m_appProperties = new Properties();
    private static String m_appId;
    private static AtomicBoolean init = new AtomicBoolean(false);

    public static void initialize() {
        if (init.compareAndSet(false, true)) {
            try {
                InputStream in = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(APP_PROPERTIES_CLASSPATH.substring(1));
                if (in == null) {
                    in = ConfigUtil.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
                }

                initialize(in);
            } catch (Throwable ex) {
                logger.error("Initialize DefaultApplicationProvider failed.", ex);
            }
            if (StringUtils.isEmpty(System.getProperty("spring.application.name"))) {
                System.getProperties().setProperty("spring.application.name", m_appId);
            }
        }
    }

    public static void initialize(InputStream in) {
        try {
            if (in != null) {
                try {
                    m_appProperties
                            .load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
                } finally {
                    in.close();
                }
            }

            initAppId();
        } catch (Throwable ex) {
            logger.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }

    private static void initAppId() {
        // 1. Get app.id from System Property
        m_appId = System.getProperty(APP_ID);
        if (Strings.isNotEmpty(m_appId)) {
            m_appId = m_appId.trim();
            logger.info("App ID is set to {} by app.id property from System Property", m_appId);
            return;
        }

        //2. Try to get app id from OS environment variable
        m_appId = System.getenv(APP_ID_ENVIRONMENT_VARIABLES);
        if (Strings.isNotEmpty(m_appId)) {
            m_appId = m_appId.trim();
            logger.info("App ID is set to {} by APP_ID property from OS environment variable", m_appId);
            return;
        }

        // 3. Try to get app id from app.properties.
        m_appId = m_appProperties.getProperty(APP_ID);
        if (Strings.isNotEmpty(m_appId)) {
            m_appId = m_appId.trim();
            logger.info("App ID is set to {} by app.id property from {}", m_appId,
                    APP_PROPERTIES_CLASSPATH);
            return;
        }
    }

    /**
     * Get the app id for the current application.
     *
     * @return the app id or ConfigConsts.NO_APPID_PLACEHOLDER if app id is not available
     */
    public static String getAppId() {
        return m_appId;
    }

    public static Env getEnv() {
        Env env = Env.CUS;
        for (Env value : Env.values()) {
            if (value.name().equalsIgnoreCase(System.getProperty("env", ""))) {
                env = value;
                break;
            }
        }
        return env;
    }
}
