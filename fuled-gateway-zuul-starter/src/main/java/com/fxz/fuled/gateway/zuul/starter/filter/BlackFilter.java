package com.fxz.fuled.gateway.zuul.starter.filter;

import com.alibaba.fastjson.JSONObject;
import com.fxz.fuled.gateway.zuul.starter.access.EndpointAccess;
import com.fxz.fuled.gateway.zuul.starter.constant.Constant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;

@Component
public class BlackFilter extends ZuulFilter implements InitializingBean {
    @Value("${fuled.zuul.filter.black.enabled:true}")
    private boolean enabled;

    @Autowired
    List<EndpointAccess> accessList;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return Constant.BASE_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return enabled;
    }

    @Override
    public Object run() throws ZuulException {
        //黑白名单拦截
        if (!CollectionUtils.isEmpty(accessList)) {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            for (EndpointAccess e : accessList) {
                try {
                    boolean passed = e.enabled() && e.access(request);
                    if (!passed) {
                        return sendBack(ctx, "unpass");
                    }
                } catch (Exception ex) {
                    return sendBack(ctx, ex.getMessage());
                }
            }
        }
        return null;
    }

    private Object sendBack(RequestContext ctx, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        ctx.setSendZuulResponse(false);
        ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        ctx.setResponseBody(jsonObject.toJSONString());
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(accessList)) {
            accessList.sort(Comparator.comparingInt(endpointAccess -> endpointAccess.order()));
        }
    }
}
