package com.fxz.fuled.dynamic.rocket.anno;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;

import java.lang.annotation.*;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicMessageListener {

    RocketMQMessageListener[] listeners() default {};
}
