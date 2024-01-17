package com.fxz.fuled.common.cache.expr;

import com.fxz.fuled.common.cache.annotation.CacheParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Parameter;
import java.util.Objects;

public class Evaluate {
    private static ExpressionParser parser = new SpelExpressionParser();

    public static <T> T evaluate(ProceedingJoinPoint proceedingJoinPoint, String expression, Class clazz) {
        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        if (Objects.nonNull(parameterNames)) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], proceedingJoinPoint.getArgs()[i]);
            }
        }
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        if (Objects.nonNull(parameters)) {
            for (int i = 0; i < parameters.length; i++) {
                context.setVariable(parameters[i].getName(), proceedingJoinPoint.getArgs()[i]);
                CacheParam cacheParam = parameters[i].getAnnotation(CacheParam.class);
                if (Objects.nonNull(cacheParam) && StringUtils.hasText(cacheParam.value())) {
                    context.setVariable(cacheParam.value(), proceedingJoinPoint.getArgs()[i]);
                }
            }
        }
        return (T) parser.parseExpression(expression).getValue(context, clazz);
    }
}
