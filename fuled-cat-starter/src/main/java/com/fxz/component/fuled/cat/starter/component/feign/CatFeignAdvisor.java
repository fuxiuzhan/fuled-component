package com.fxz.component.fuled.cat.starter.component.feign;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @author fuled
 */
public class CatFeignAdvisor extends DefaultPointcutAdvisor {
    public CatFeignAdvisor() {
        this.setAdvice(new CatFeignMethodInterceptor());
        this.setOrder(0);
    }

    public CatFeignAdvisor(Pointcut pointcut) {
        this();
        this.setPointcut(pointcut);
    }
}
