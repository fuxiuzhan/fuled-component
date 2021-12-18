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
import org.springframework.util.StopWatch;

/**
 *
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class MethodMonitorAspect {
    @Value("${method.monitor.enabled:false}")
    private boolean monitorEnabled;

    @Bean("LoggerVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-logger.version", "1.0.0.waterdrop", "fuled-logger-component");
    }

    @Around("@annotation(com.fxz.fuled.logger.starter.annotation.Monitor)")
    public Object monitorAnno(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return process(proceedingJoinPoint, true);
    }

    private Object process(ProceedingJoinPoint proceedingJoinPoint, boolean isAnno) throws Throwable {
        if (!monitorEnabled && !isAnno) {
            return proceedingJoinPoint.proceed();
        } else {
            String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            boolean printParams = true;
            boolean printRtns = true;
            if (isAnno) {
                Monitor monitor = methodSignature.getMethod().getAnnotation(Monitor.class);
                printParams = monitor.printParams();
                printRtns = monitor.printResult();
            }
            String methodName = methodSignature.getName();
            String returnType = methodSignature.getReturnType().getTypeName();
            Object result = null;
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
                log.info("Monitor: class->{},method->{},params->{},result->{},resultType->{},timeElapsed->{} ms,isSecc->{},errorMsg->{}", className, methodName, params, resultJson, returnType, stopWatch.getTotalTimeMillis(), resultFlag, errorMsg);
            }
        }
    }
}
