package com.fxz.component.fuled.cat.starter.util;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author fuled
 */
@Component
public class CatAspectUtil {
    private static List<String> exceptionClassIgnore;
    private static List<String> exceptionMsgIgnore;

    @Value("${cat.aspect.exception.class.ignore:}")
    public void setExceptionIgnore(List<String> value) {
        exceptionClassIgnore = value;
    }

    @Value("${cat.aspect.exception.msg.ignore:}")
    public void setExceptionMsgIgnore(List<String> value) {
        exceptionMsgIgnore = value;
    }


    public static Object aspectLogic(ProceedingJoinPoint pjp, Object[] args, Class annotation) throws Throwable {
        String name = pjp.getSignature().getDeclaringType().getSimpleName() + "." + pjp.getSignature().getName();
        Transaction transaction = Cat.newTransaction(annotation.getTypeName(), name);
        return aspectLogic(pjp, args, transaction);
    }

    public static Object aspectLogic(ProceedingJoinPoint pjp, Object[] args, Transaction transaction) throws Throwable {
        Object proceed;
        try {
            if (args != null && args.length != 0) {
                proceed = pjp.proceed(args);
            } else {
                proceed = pjp.proceed();
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            if (shouldSkip(e)) {
                transaction.setStatus(Transaction.SUCCESS);
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
        Object proceed;
        try {
            proceed = invocation.proceed();
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            if (shouldSkip(e)) {
                transaction.setStatus(Transaction.SUCCESS);
            } else {
                transaction.setStatus(e);
            }
            throw e;
        } finally {
            transaction.complete();
        }
        return proceed;
    }

    /**
     * @param e
     * @return
     */
    private static boolean shouldSkip(Throwable e) {
        if (Objects.nonNull(exceptionClassIgnore) && exceptionClassIgnore.size() > 0 && exceptionClassIgnore.contains(e.getClass().getName())) {
            return Boolean.TRUE;
        }
        if (Objects.nonNull(exceptionMsgIgnore) && exceptionMsgIgnore.size() > 0) {
            for (String s : exceptionMsgIgnore) {
                if (e.getMessage().contains(s)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
}
