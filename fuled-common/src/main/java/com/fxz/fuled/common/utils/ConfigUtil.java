
package com.fxz.fuled.common.utils;


import com.fxz.fuled.common.Env;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;


@Order(Ordered.HIGHEST_PRECEDENCE)
public class ConfigUtil implements ApplicationContextAware {

    public static ApplicationContext applicationContext;

    public static Environment environment;

    @Autowired
    private Environment envInject;

    public static final String APP_NAME = "spring.application.name";

    public static final String ENV = "env";

    @PostConstruct
    public void init() {
        ConfigUtil.environment = envInject;
    }

    public static String getAppId() {
        return environment.getProperty(APP_NAME);
    }

    public static Env getEnv() {
        Env env = Env.CUS;
        for (Env value : Env.values()) {
            if (value.name().equalsIgnoreCase(System.getProperty(ENV, ""))) {
                env = value;
                break;
            }
        }
        return env;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigUtil.applicationContext = applicationContext;
    }
}
