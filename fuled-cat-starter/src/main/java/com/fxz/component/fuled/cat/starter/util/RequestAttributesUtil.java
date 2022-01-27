package com.fxz.component.fuled.cat.starter.util;

import org.springframework.web.context.request.AbstractRequestAttributes;
import org.springframework.web.context.request.RequestAttributes;

import java.lang.reflect.Method;

/**
 * @author fxz
 */
public class RequestAttributesUtil {
    private static Method isRequestActiveMethod;

    public RequestAttributesUtil() {
    }

    public static boolean isRequestActive(RequestAttributes attributes) {
        if (isRequestActiveMethod == null) {
            try {
                isRequestActiveMethod = AbstractRequestAttributes.class.getDeclaredMethod("isRequestActive");
                isRequestActiveMethod.setAccessible(true);
            } catch (Exception var3) {
            }
        }

        if (attributes instanceof AbstractRequestAttributes) {
            try {
                return (Boolean) isRequestActiveMethod.invoke(attributes);
            } catch (Exception var2) {
                return false;
            }
        } else {
            return false;
        }
    }
}
