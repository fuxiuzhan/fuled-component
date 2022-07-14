package com.fxz.fuled.gateway.zuul.starter.filter;

import com.fxz.fuled.common.utils.Result;
import com.fxz.fuled.gateway.zuul.starter.auth.AuthService;
import com.fxz.fuled.gateway.zuul.starter.constant.Constant;
import com.fxz.fuled.gateway.zuul.starter.pojo.JwtInfo;
import com.fxz.fuled.gateway.zuul.starter.utils.RequestUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * token 检查 OPTION 直接通过
 */
@Component
@Slf4j
public class TokenFilter extends ZuulFilter {

    @Autowired
    AuthService authService;

    @Value("${fuled.zuul.filter.token.enabled:true}")
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
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        ctx.addZuulRequestHeader("Proxy-Client-IP", RequestUtils.getClientIp(request));
        String token = RequestUtils.accessToken(request);
        Result<JwtInfo> jwtInfoResult = authService.getByToken(token, request.getRequestURI());

        if (jwtInfoResult.isSuccess() && Objects.nonNull(jwtInfoResult.getData())) {
            JwtInfo jwtInfo = jwtInfoResult.getData();
            ctx.addZuulRequestHeader(Constant.HEADER_USER_NAME, jwtInfo.getUserName());
            ctx.addZuulRequestHeader(Constant.HEADER_USER_ID, jwtInfo.getUserId());
            ctx.addZuulRequestHeader(Constant.HEADER_USER_ALIAS, jwtInfo.getAlias());
            ctx.set(Constant.AUTH_RESULT, Boolean.TRUE);
        }
        return null;
    }
}
