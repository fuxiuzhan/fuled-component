package com.fxz.fuled.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationUtil implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationUtil.applicationContext = applicationContext;
    }
}
