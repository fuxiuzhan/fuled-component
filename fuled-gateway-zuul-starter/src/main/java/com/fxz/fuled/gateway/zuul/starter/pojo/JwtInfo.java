package com.fxz.fuled.gateway.zuul.starter.pojo;

import lombok.Data;

@Data
public class JwtInfo {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 别名
     */
    private String alias;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户手机号（掩码）
     */
    private String phone;
    /**
     * 部门
     */
    private String dept;
}
