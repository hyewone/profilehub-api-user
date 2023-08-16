package com.goorm.profileboxapiuser.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    // Request 헤더에서 JwtToken 가져오기
    public String getJwtAccessTokenFromHeader(HttpServletRequest request){

        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        String jwtToken = "";

        if(jwtHeader != null && jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)){
            jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX,"");
        }
        return jwtToken;
    }

    // Request 쿠키에서 JwtToken 가져오기
    public String getJwtAccessTokenFromCookie(HttpServletRequest request){
        String clientJwtToken = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JwtProperties.ACCESS_TOKEN_COOKIE)) {
                    clientJwtToken = cookie.getValue();
                    break;
                }
            }
        }
        return clientJwtToken;
    }
}
