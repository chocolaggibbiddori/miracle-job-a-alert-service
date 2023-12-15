package com.example.alertservice.jwt.service;

import com.example.alertservice.jwt.JwtProvider;
import com.example.alertservice.jwt.repository.JwtRepository;
import com.example.alertservice.jwt.domain.AccessToken;
import com.example.alertservice.jwt.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtRepository jwtRepository;
    private final JwtProvider jwtProvider;

    public AccessToken createToken(String memberType, String email, Map<String, Object> claims) {
        Objects.requireNonNull(memberType, "Member type is null");
        Objects.requireNonNull(email, "Email is null");

        String subject = memberType + ":" + email;
        Map<String, String> tokens = jwtProvider.createTokens(subject, claims);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");
        jwtRepository.save(subject, new RefreshToken(refreshToken));
        return new AccessToken(accessToken);
    }

    public boolean validateToken(String token, boolean gatewayToken) {
        return jwtProvider.validateToken(token, gatewayToken);
    }

    public AccessToken refreshToken(String refreshToken, String subject, Map<String, Object> claims) {
        String refreshedAccessToken = jwtProvider.refreshAccessToken(refreshToken, subject, claims);
        return new AccessToken(refreshedAccessToken);
    }
}
