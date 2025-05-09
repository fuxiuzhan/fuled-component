package com.fxz.component.fuled.cat.starter.component.http;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.fxz.component.fuled.cat.starter.util.CatPropertyContext;
import com.fxz.component.fuled.cat.starter.util.CatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author fxz
 */
public class HttpCatCrossFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(HttpCatCrossFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String requestURI = request.getRequestURI();
        String root = request.getHeader(Cat.Context.ROOT);
        String parent = request.getHeader(Cat.Context.PARENT);
        String child = request.getHeader(Cat.Context.CHILD);
        if (!StringUtils.isEmpty(root) && !StringUtils.isEmpty(parent) && !StringUtils.isEmpty(child)) {
            Transaction t = Cat.newTransaction("ServiceProvider", requestURI);
            try {
                Cat.Context context = new CatPropertyContext();
                context.addProperty(Cat.Context.ROOT, root);
                context.addProperty(Cat.Context.PARENT, parent);
                context.addProperty(Cat.Context.CHILD, child);
                Cat.logRemoteCallServer(context);
                CatUtils.createProviderCross(request, t);
                filterChain.doFilter(req, resp);
                t.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                log.warn("------ Get cat msgtree error : ", e);
                Event event = Cat.newEvent("HTTP_REST_CAT_ERROR", requestURI);
                event.setStatus(e);
                CatUtils.completeEvent(event);
                t.addChild(event);
                t.setStatus(e.getClass().getSimpleName());
            } finally {
                t.complete();
            }
        } else {
            Cat.getManager().setTraceMode(true);
            filterChain.doFilter(req, resp);
        }
    }

    @Override
    public void init(FilterConfig arg0) {
    }

    @Override
    public void destroy() {
    }
}

