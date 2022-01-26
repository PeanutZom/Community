package com.xinhao.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Xinhao
 * @Date 2022/1/26
 * @Descrption
 */
public class CookieUtil {
    public static Cookie getCookie(HttpServletRequest request, String key){
        Cookie[] cookies = request.getCookies();
        if(key==null){
            throw new IllegalArgumentException("cookie名不能为null");
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(key)){
                return cookie;
            }
        }
        return null;
    }
}
