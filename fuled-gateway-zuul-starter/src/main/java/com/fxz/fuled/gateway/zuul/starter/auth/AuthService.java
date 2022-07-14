package com.fxz.fuled.gateway.zuul.starter.auth;


import com.fxz.fuled.common.utils.Result;
import com.fxz.fuled.gateway.zuul.starter.pojo.JwtInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-server")
public interface AuthService {

    /**
     * @param token
     * @param url
     * @return
     */
    @PostMapping("/user/token")
    Result<JwtInfo> getByToken(@RequestParam("token") String token, @RequestParam("url") String url);
}
