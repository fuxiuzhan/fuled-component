package com.fxz.component.fuled.cat.starter.component.template.rest;


import com.fxz.component.fuled.cat.starter.util.CatUtils;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

/**
 * @author fuled
 */
public class CatHystrixCommandAspect extends HystrixCommandAspect {
    @Override
    @Around("hystrixCommandAnnotationPointcut() || hystrixCollapserAnnotationPointcut()")
    public Object methodsAnnotatedWithHystrixCommand(ProceedingJoinPoint joinPoint) throws Throwable {
        CatUtils.createMessageTree();
        return super.methodsAnnotatedWithHystrixCommand(joinPoint);
    }
}