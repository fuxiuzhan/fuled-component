package com.fxz.component.fuled.cat.starter.util;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;

@Component
public class CatAspectUtil {
    private static List<String> exceptionIgnore;

    public CatAspectUtil() {
    }

    @Value("#{'${cat.aspect.exception.ignore:,}'.replaceAll('[ \\n\\t]', '').split(',', 0)}")
    public void setExceptionIgnore(List<String> value) {
        exceptionIgnore = value;
    }

    public static Object aspectLogic(ProceedingJoinPoint pjp, Object[] args, Class annotation) throws Throwable {
        String name = pjp.getSignature().getDeclaringType().getSimpleName() + "." + pjp.getSignature().getName();
        Transaction transaction = Cat.newTransaction(annotation.getTypeName(), name);
        return aspectLogic(pjp, args, transaction);
    }

    public static Object aspectLogic(ProceedingJoinPoint pjp, Object[] args, Transaction transaction) throws Throwable {
        Object var4;
        try {
            Object proceed;
            if (args != null && args.length != 0) {
                proceed = pjp.proceed(args);
            } else {
                proceed = pjp.proceed();
            }

            transaction.setStatus("0");
            var4 = proceed;
        } catch (Throwable var8) {
            if (exceptionIgnore != null && exceptionIgnore.size() > 0 && exceptionIgnore.contains(var8.getClass().getName())) {
                transaction.setStatus("0");
            } else {
                transaction.setStatus(var8);
            }

            throw var8;
        } finally {
            transaction.complete();
        }

        return var4;
    }

    public static Object aspectLogic(MethodInvocation invocation, String type) throws Throwable {
        Method method = invocation.getMethod();
        Class clazz = method.getDeclaringClass();
        Transaction transaction = Cat.newTransaction(type, clazz.getSimpleName() + "." + method.getName());

        Object var6;
        try {
            Object proceed = invocation.proceed();
            transaction.setStatus("0");
            var6 = proceed;
        } catch (Throwable var10) {
            if (exceptionIgnore != null && exceptionIgnore.size() > 0 && exceptionIgnore.contains(var10.getClass().getName())) {
                transaction.setStatus("0");
            } else {
                transaction.setStatus(var10);
            }

            throw var10;
        } finally {
            transaction.complete();
        }

        return var6;
    }
}
