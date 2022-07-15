package com.fxz.fuled.oauth.server.starter.auth;

/**
 * 用户认证
 *
 * 返回用户对应的token信息
 * @param <REQ>
 * @param <RES>
 * @author fxz
 */
public interface UserAuth<REQ, RES> {
    /**
     *  根据用户验证信息生成token
     * @param req
     * @return
     */
    RES generateToken(REQ req);
}
