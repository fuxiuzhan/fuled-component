package com.fxz.fuled.gateway.zuul.starter.access;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fxz
 * <p>
 * 访问限制，ip黑白名单
 * <p>
 * 拦截可以分多种方式
 * 1、完全匹配型（黑白名单）
 * 2、只校验黑名单
 * 3、只校验白名单
 */
public interface EndpointAccess {

    /**
     * 根据请求确定是否放行
     *
     * @param request
     * @return
     */
    boolean access(HttpServletRequest request);

    /**
     * 是否启用
     *
     * @return
     */
    boolean enabled();

    /**
     * order
     *
     * @return
     */
    int order();
}
