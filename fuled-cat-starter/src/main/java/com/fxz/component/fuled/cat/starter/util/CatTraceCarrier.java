package com.fxz.component.fuled.cat.starter.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * trace载体，取代RequestContextHolder.getRequestAttributes
 */
public class CatTraceCarrier {

    private static final ThreadLocal<Context> contextThreadLocal = ThreadLocal.withInitial(() -> new Context());

    /**
     * @return
     */
    public static Context getContext() {
        return contextThreadLocal.get();
    }

    /**
     * @param context
     */
    public static void setContext(Context context) {
        contextThreadLocal.set(context);

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Context {
        private String rootTrace;
        private String parentTrace;
        private String childTrace;
        private String appName;
        private Map<String, Object> extPrams = new HashMap<>();
    }
}
