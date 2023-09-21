package com.goorm.profileboxapiuser.auth;


import com.goorm.profileboxcomm.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;
    private final String[] allowedUrls = {"/"
                            , "/v1/profile/profiles"
                            , "/v1/notice/notices"
                            , "/swagger-ui/**"
                            , "/swagger-resources/**"
                            , "/v3/api-docs/**"
    };

    public AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .addFilter(corsFilter)
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtProvider, restTemplate))
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeHttpRequests()
                .requestMatchers(allowedUrls).permitAll()
//                .anyRequest().permitAll()
                .anyRequest().authenticated()
                .and()
                .build();

    }

}
