package com.fxz.component.fuled.cat.starter.component.http;

import com.dianping.cat.Cat;
import com.dianping.cat.message.spi.MessageTree;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpCatAfterFilter implements Filter {
    public HttpCatAfterFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            MessageTree messageTree = Cat.getManager().getThreadLocalMessageTree();
            String catRootId = messageTree.getRootMessageId();
            String catParentId = messageTree.getParentMessageId();
            String catId = messageTree.getMessageId();
            if (!StringUtils.isEmpty(catId)) {
                MDC.put("X-CAT-ID", catId);
            }

            if (!StringUtils.isEmpty(catRootId)) {
                MDC.put("X-CAT-ROOT-ID", catRootId);
            } else if (!StringUtils.isEmpty(catId)) {
                MDC.put("X-CAT-ROOT-ID", catId);
            }

            if (!StringUtils.isEmpty(catParentId)) {
                MDC.put("X-CAT-PARENT-ID", catParentId);
            } else if (!StringUtils.isEmpty(catId)) {
                MDC.put("X-CAT-PARENT-ID", catId);
            }

            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setHeader("X-CAT-SERVER", "");
            filterChain.doFilter(servletRequest, httpServletResponse);
        } finally {
            MDC.clear();
        }

    }

    @Override
    public void destroy() {

    }
}
