package com.fxz.fuled.gateway.zuul.starter.filter;

import com.alibaba.fastjson.JSONObject;
import com.fxz.fuled.common.utils.ResultEnum;
import com.fxz.fuled.gateway.zuul.starter.constant.Constant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * 黑白名单限制
 */

@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

    @Value("${fuled.zuul.filter.access.enabled:true}")
    private boolean enabled;

    @Value("${fuled.zuul.help.url:}")
    private String helpUrl;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return Constant.BASE_ORDER + 10;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return enabled && !ctx.getBoolean(Constant.AUTH_RESULT);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("helpUrl", helpUrl);
        jsonObject.put("code", ResultEnum.USER_NOT_SIGN_IN.getCode());
        jsonObject.put("message", ResultEnum.USER_NOT_SIGN_IN.getMessage());
        ctx.setSendZuulResponse(false);
        ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        ctx.setResponseBody(jsonObject.toJSONString());
        return null;
    }
}
