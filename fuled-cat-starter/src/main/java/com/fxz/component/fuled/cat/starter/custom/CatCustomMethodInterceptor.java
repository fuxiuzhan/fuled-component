package com.fxz.component.fuled.cat.starter.custom;

import com.fxz.component.fuled.cat.starter.annotation.IgnoreCatTracing;
import com.fxz.component.fuled.cat.starter.util.CatAspectUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author fxz
 */
public class CatCustomMethodInterceptor implements MethodInterceptor {
    private Class classAnnotationType;

    public CatCustomMethodInterceptor(Class<? extends Annotation> classAnnotationType) {
        this.classAnnotationType = classAnnotationType;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class clazz = method.getDeclaringClass();
        if (clazz.isAssignableFrom(Object.class)) {
            return invocation.proceed();
        } else {
            IgnoreCatTracing ignoreCatTracing = (IgnoreCatTracing)method.getAnnotation(IgnoreCatTracing.class);
            if (ignoreCatTracing == null) {
                ignoreCatTracing = (IgnoreCatTracing)clazz.getAnnotation(IgnoreCatTracing.class);
            }

            String type = this.classAnnotationType != null ? this.classAnnotationType.getSimpleName() : "MethodInvocation";
            return ignoreCatTracing == null ? CatAspectUtil.aspectLogic(invocation, type) : invocation.proceed();
        }
    }
}
