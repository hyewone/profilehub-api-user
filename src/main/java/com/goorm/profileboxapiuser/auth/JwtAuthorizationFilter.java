package com.goorm.profileboxapiuser.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.profileboxapiuser.service.MemberService;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.exception.ApiExceptionEntity;
import com.goorm.profileboxcomm.exception.ExceptionEnum;
import com.goorm.profileboxcomm.response.ApiResult;
import com.goorm.profileboxcomm.response.ApiResultType;
import com.goorm.profileboxcomm.service.MemberRedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberService memberService;
    private MemberRedisService memberRedisService;
    private JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberService memberService, JwtProvider jwtProvider, MemberRedisService memberRedisService) {
        super(authenticationManager);
        this.memberService = memberService;
        this.memberRedisService = memberRedisService;
        this.jwtProvider = jwtProvider;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        //        String jwtToken = jwtProvider.getJwtAccessTokenFromCookie(request);
        String jwtToken = jwtProvider.getJwtAccessTokenFromHeader(request);


        if (jwtToken.equals("")) {
            chain.doFilter(request, response);
            return;
        }
        String email = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken).getClaim("email").asString();

        if (email != null) {
            Member member = memberRedisService.getMemberFromRedis(jwtToken);
            if(member == null){
                member = memberService.findLoginMemberByEmail(email);
//                member = memberService.findLoginMemberByEmail(email, jwtToken);
            }
            PrincipalDetails princialDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(princialDetails, null, princialDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }
    }

    protected String getResponseErrorString(Exception e, ExceptionEnum exceptionEnum) throws JsonProcessingException {
        ApiExceptionEntity apiExceptionEntity = ApiExceptionEntity.builder()
                .errorMessage(e.getMessage())
                .build();
        e.printStackTrace();
        ResponseEntity re = ResponseEntity
                .status(exceptionEnum.getStatus())
                .body(ApiResult.builder()
                        .status(ApiResultType.ERROR.toString())
                        .statusCode(exceptionEnum.getStatus().value())
                        .message(apiExceptionEntity.getErrorMessage())
                        .build());


        ObjectMapper objectMapper = new ObjectMapper();
        String resStr = objectMapper.writeValueAsString(re);
        return resStr;
    }
}
