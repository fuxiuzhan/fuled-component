package com.fxz.fuled.gateway.zuul.starter.utils;

import com.fxz.fuled.gateway.zuul.starter.constant.Constant;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;


@Slf4j
public abstract class RequestUtils {

    public static String accessToken(HttpServletRequest request) {
        return request.getHeader(Constant.HEADER_ACCESS_TOKEN);
    }

    public static String userName(HttpServletRequest request) {
        return request.getHeader(Constant.HEADER_USER_NAME);
    }


    public static String userId(HttpServletRequest request) {
        return request.getHeader(Constant.HEADER_USER_ID);
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }

}