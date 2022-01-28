package com.fxz.component.fuled.cat.starter.custom;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

import java.lang.annotation.Annotation;

public class CatCustomAdvisor extends DefaultPointcutAdvisor {
    public CatCustomAdvisor(Class<? extends Annotation> classAnnotationType) {
        this(classAnnotationType, (Class)null);
    }

    public CatCustomAdvisor(Class<? extends Annotation> classAnnotationType, Class<? extends Annotation> methodAnnotationType) {
        ComposablePointcut pointcut = null;
        Pointcut cpc = null;
        Pointcut mpc = null;
        if (classAnnotationType != null) {
            cpc = new AnnotationMatchingPointcut(classAnnotationType, true);
        }

        if (methodAnnotationType != null) {
            mpc = AnnotationMatchingPointcut.forMethodAnnotation(methodAnnotationType);
        }

        if (cpc != null) {
            pointcut = new ComposablePointcut(cpc);
        }

        if (mpc != null) {
            if (pointcut == null) {
                pointcut = new ComposablePointcut(mpc);
            } else {
                pointcut = pointcut.union(mpc);
            }
        }

        if (classAnnotationType != null) {
            this.setAdvice(new CatCustomMethodInterceptor(classAnnotationType));
        } else {
            this.setAdvice(new CatCustomMethodInterceptor(methodAnnotationType));
        }

        this.setPointcut(pointcut);
        this.setOrder(0);
    }
}
