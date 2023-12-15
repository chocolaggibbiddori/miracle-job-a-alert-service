package com.example.alertservice.jwt.config;

import com.example.alertservice.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtProvider jwtProvider(@Value("${jwt.secret}") String secretKey,
                                   @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
                                   @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        return new JwtProvider(secretKey, accessTokenValidityInSeconds, refreshTokenValidityInSeconds);
    }
}
