package com.fxz.fuled.gateway.zuul.starter.auth;


import com.fxz.fuled.gateway.zuul.starter.pojo.JwtInfo;

public interface AuthService {
   JwtInfo getByToken( String token,String url);
}
