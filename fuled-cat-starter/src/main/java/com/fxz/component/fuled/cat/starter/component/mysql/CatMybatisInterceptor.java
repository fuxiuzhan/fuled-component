package com.fxz.component.fuled.cat.starter.component.mysql;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author fuled
 */
@Intercepts({@Signature(
        method = "query",
        type = Executor.class,
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        method = "query",
        type = Executor.class,
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
), @Signature(
        method = "update",
        type = Executor.class,
        args = {MappedStatement.class, Object.class}
)})
public class CatMybatisInterceptor implements Interceptor {
    private Executor target = null;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = this.getStatement(invocation);
        String methodName = this.getMethodName(mappedStatement);
        Transaction t = Cat.newTransaction("SQL", methodName);
        String sql = this.getSql(invocation, mappedStatement);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Cat.logEvent("SQL.Method", sqlCommandType.name().toLowerCase(), "0", sql);
        return this.doFinish(invocation, t);
    }

    private MappedStatement getStatement(Invocation invocation) {
        return (MappedStatement) invocation.getArgs()[0];
    }

    private String getMethodName(MappedStatement mappedStatement) {
        String[] strArr = mappedStatement.getId().split("\\.");
        String methodName = strArr[strArr.length - 2] + "." + strArr[strArr.length - 1];
        return methodName;
    }

    private String getSql(Invocation invocation, MappedStatement mappedStatement) {
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        return sql;
    }

    private Object doFinish(Invocation invocation, Transaction t) throws InvocationTargetException, IllegalAccessException {
        Object returnObj = null;
        try {
            returnObj = invocation.proceed();
            t.setStatus("0");
        } catch (Exception e) {
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
        return returnObj;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            this.target = (Executor) target;
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
