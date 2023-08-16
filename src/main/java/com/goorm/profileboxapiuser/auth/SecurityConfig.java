package com.goorm.profileboxapiuser.auth;


import com.goorm.profileboxapiuser.service.MemberService;
import com.goorm.profileboxcomm.service.MemberRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final MemberService memberService;
    private final MemberRedisService memberRedisService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtProvider jwtProvider;
    private final String[] allowedUrls = {"/"
                            , "/v1/open/**"
                            , "/swagger-ui/**"
                            , "/swagger-resources/**"
                            , "/v3/api-docs/**"
    };

    public AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.httpBasic().disable()
                .csrf().disable()
//                .cors()
//                .and()
                .addFilter(corsFilter)
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), memberService, jwtProvider, memberRedisService))
                .authorizeHttpRequests()
                .requestMatchers(allowedUrls).permitAll()
//                .anyRequest().permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // csrf
                .and()
//                .exceptionHandling()
//                .accessDeniedHandler(new AccessDeniedHandler() {
//                    @Override
//                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//                        response.sendRedirect("/denied");
//                    }
//                }).and()
                .build();

    }

}
