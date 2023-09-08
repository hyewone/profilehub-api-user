package com.goorm.profileboxapiuser.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goorm.profileboxapiuser.service.MemberService;
import com.goorm.profileboxcomm.auth.JwtProperties;
import com.goorm.profileboxcomm.auth.JwtProvider;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.exception.ApiExceptionEntity;
import com.goorm.profileboxcomm.exception.ExceptionEnum;
import com.goorm.profileboxcomm.response.ApiResult;
import com.goorm.profileboxcomm.response.ApiResultType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberService memberService, JwtProvider jwtProvider, RestTemplate restTemplate) {
        super(authenticationManager);
        this.memberService = memberService;
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String jwtToken = jwtProvider.getJwtAccessTokenFromHeader(request)
                .orElse("");

        if(jwtToken.equals("")){
            chain.doFilter(request, response);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, JwtProperties.TOKEN_PREFIX + jwtToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> authResponse = restTemplate.exchange(
                "http://localhost:7002" + "/v1/auth/verify",
                HttpMethod.GET,
                entity,
                String.class
        );

        // auth 서버 응답을 처리
        if (authResponse.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            Member member = objectMapper.readValue(authResponse.getBody(), Member.class);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(member.getMemberType().toString()));

            Authentication authentication = new UsernamePasswordAuthenticationToken(member, member.getMemberEmail(), authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }else{
            chain.doFilter(request, response);
        }
    }
}
