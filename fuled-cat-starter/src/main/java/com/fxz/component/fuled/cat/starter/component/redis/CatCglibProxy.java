package com.fxz.component.fuled.cat.starter.component.redis;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author fxz
 */
public class CatCglibProxy implements MethodInterceptor {
    private Object target;

    public Object getInstance(Object target, Class[] argumentTypes, Object[] arguments) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return enhancer.create(argumentTypes, arguments);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();
        if (this.methodFilterCheck(methodName)) {
            return methodProxy.invokeSuper(o, objects);
        } else {
            Transaction catTransaction = Cat.newTransaction("Cache.Redis", methodName);

            Object result;
            try {
                Cat.logEvent("Cache.Redis.args", this.parameterBuilder(objects));
                result = methodProxy.invokeSuper(o, objects);
                Cat.logEvent("Cache.Redis.result", result != null ? result.toString() : null);
                catTransaction.setStatus("0");
            } catch (Throwable e) {
                catTransaction.setStatus(e);
                throw e;
            } finally {
                catTransaction.complete();
            }
            return result;
        }
    }

    private String parameterBuilder(Object[] objects) {
        if (objects != null && objects.length > 0) {
            StringBuffer sb = new StringBuffer();
            Object[] var3 = objects;
            int var4 = objects.length;
            for (int var5 = 0; var5 < var4; ++var5) {
                Object o = var3[var5];
                if (!StringUtils.isEmpty(sb.toString())) {
                    sb.append("&");
                }
                if (Objects.nonNull(o)) {
                    sb.append(o.toString());
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private boolean methodFilterCheck(String methodName) {
        String[] filters = new String[]{"rawKey", "rawHashValue", "keySerializer", "rawHashKey", "hashKeySerializer", "execute", "toString", "hashCode", "hashValueSerializer", "valueSerializer", "stringSerializer", "getOperations", "rawString", "rawValue", "rawValues", "rawHashKeys", "rawKeys", "deserializeValues", "deserializeTupleValues", "deserializeTuple", "rawTupleValues", "deserializeHashKeys", "deserializeHashValues", "deserializeHashMap", "deserializeKey", "deserializeKeys", "deserializeValue", "deserializeString", "deserializeHashKey", "deserializeHashValue", "deserializeGeoResults"};
        List<String> list = CollectionUtils.arrayToList(filters);
        return list.contains(methodName);
    }
}
