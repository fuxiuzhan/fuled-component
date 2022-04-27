package com.fxz.component.fuled.skywalking.starter.filter;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fxz
 * <p>
 * 将traceId传给其他系统或者前端
 */
public class TraceIdFilter implements Filter {
    private static final String TRACE_ID = "SW_TRACE_ID";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Trace
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (!response.getHeaderNames().contains(TRACE_ID)) {
            response.addHeader(TRACE_ID, TraceContext.traceId());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
