package com.fxz.fuled.gateway.zuul.starter.filter;

import com.fxz.fuled.gateway.zuul.starter.constant.Constant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

@Component
public class BlackFilter extends ZuulFilter {
    @Value("${fuled.zuul.filter.balck.enabled:true}")
    private boolean enabled;

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
        //黑名单拦截
        return null;
    }
}
