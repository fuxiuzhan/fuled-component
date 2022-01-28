package com.fxz.component.fuled.cat.starter.util;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author fuled
 */
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
        Object proceed = null;
        try {
            if (args != null && args.length != 0) {
                proceed = pjp.proceed(args);
            } else {
                proceed = pjp.proceed();
            }
            transaction.setStatus("0");
        } catch (Throwable e) {
            if (exceptionIgnore != null && exceptionIgnore.size() > 0 && exceptionIgnore.contains(e.getClass().getName())) {
                transaction.setStatus("0");
            } else {
                transaction.setStatus(e);
            }
            throw e;
        } finally {
            transaction.complete();
        }
        return proceed;
    }

    public static Object aspectLogic(MethodInvocation invocation, String type) throws Throwable {
        Method method = invocation.getMethod();
        Class clazz = method.getDeclaringClass();
        Transaction transaction = Cat.newTransaction(type, clazz.getSimpleName() + "." + method.getName());
        Object proceed = null;
        try {
            proceed = invocation.proceed();
            transaction.setStatus("0");
        } catch (Throwable e) {
            if (exceptionIgnore != null && exceptionIgnore.size() > 0 && exceptionIgnore.contains(e.getClass().getName())) {
                transaction.setStatus("0");
            } else {
                transaction.setStatus(e);
            }
            throw e;
        } finally {
            transaction.complete();
        }
        return proceed;
    }
}
