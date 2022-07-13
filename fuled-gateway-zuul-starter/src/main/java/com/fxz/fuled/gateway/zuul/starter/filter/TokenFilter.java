package com.fxz.fuled.gateway.zuul.starter.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * token 检查 OPTION 直接通过
 */
@Component
@Slf4j
public class TokenFilter extends ZuulFilter implements InitializingBean {

    @Value("${fuled.zuul.filter.token.enabled:true}")
    private boolean enabled;

    @Override
    public String filterType() {
        return "PRE";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return enabled;
    }

    @Override
    public Object run() throws ZuulException {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
