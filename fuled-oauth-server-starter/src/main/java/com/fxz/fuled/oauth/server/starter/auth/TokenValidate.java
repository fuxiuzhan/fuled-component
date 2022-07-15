package com.fxz.fuled.oauth.server.starter.auth;

/**
 * 验证用户token 网关鉴权时使用
 *
 * @param <REQ>
 * @param <RES>
 * @author fxz
 */
public interface TokenValidate<REQ, RES> {

    /**
     * 根据用户token 返回用户的基本信息
     * 用于网关后系统统一用户标识
     *
     * @param req
     * @return
     */
    RES getByToken(REQ req);
}
