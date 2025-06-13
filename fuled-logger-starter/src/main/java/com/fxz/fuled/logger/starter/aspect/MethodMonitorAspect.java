package com.fxz.fuled.logger.starter.aspect;


import com.alibaba.fastjson.JSON;
import com.fxz.fuled.common.version.ComponentVersion;
import com.fxz.fuled.logger.starter.annotation.Monitor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;

/**
 * @author fuxiuzhan
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class MethodMonitorAspect {
    @Value("${method.monitor.enabled:true}")
    private boolean monitorEnabled;

    @Value("${method.monitor.ignores:}")
    private List<String> ignores;

    @Bean("loggerVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-logger.version", "1.0.0.waterdrop", "fuled-logger-component");
    }

    @Around("@annotation(com.fxz.fuled.logger.starter.annotation.Monitor)")
    public Object monitor(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return process(proceedingJoinPoint, true);
    }

    private Object process(ProceedingJoinPoint proceedingJoinPoint, boolean isAnno) throws Throwable {
        if (shouldHit(proceedingJoinPoint, isAnno)) {
            String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            String methodName = methodSignature.getName();
            boolean printParams = true;
            boolean printRtns = true;
            String[] tags = {};
            if (isAnno) {
                Monitor monitor = methodSignature.getMethod().getAnnotation(Monitor.class);
                printParams = monitor.printParams();
                printRtns = monitor.printResult();
                tags = monitor.tags();
            }
            String returnType = methodSignature.getReturnType().getTypeName();
            Object result;
            String resultJson = "unPrint";
            String params = "unPrint";
            String errorMsg = null;
            if (printParams) {
                try {
                    params = JSON.toJSONString(proceedingJoinPoint.getArgs());
                } catch (Exception e) {
                }
            }
            boolean resultFlag = false;
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            try {
                result = proceedingJoinPoint.proceed();
                resultFlag = true;
                if (printRtns) {
                    try {
                        resultJson = JSON.toJSONString(result);
                    } catch (Exception e) {
                    }
                }
                return result;
            } catch (Throwable t) {
                errorMsg = t.getMessage();
                throw t;
            } finally {
                stopWatch.stop();
                log.info("Monitor: class->{},method->{},tags->{},params->{},result->{},resultType->{},timeElapsed->{} ms,isSecc->{},errorMsg->{}", className, methodName, tags, params, resultJson, returnType, stopWatch.getTotalTimeMillis(), resultFlag, errorMsg);
            }
        } else {
            return proceedingJoinPoint.proceed();
        }
    }

    /**
     * @param proceedingJoinPoint
     * @param isAnno
     * @return
     */
    private boolean shouldHit(ProceedingJoinPoint proceedingJoinPoint, boolean isAnno) {
        if (monitorEnabled && isAnno) {
            String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            String methodName = methodSignature.getName();
            if (!CollectionUtils.isEmpty(ignores) && (ignores.contains(className) || ignores.contains(className + "." + methodName))) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
