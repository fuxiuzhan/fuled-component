package com.fxz.component.fuled.skywalking.starter.webfilter;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fxz
 */
public class TraceIdFilter implements Filter {
    private static final String traceId = "SW_TRACE_ID";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Trace
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (!response.getHeaderNames().contains(traceId)) {
            response.addHeader(traceId, TraceContext.traceId());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
