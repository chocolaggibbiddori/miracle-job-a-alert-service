package com.example.util.jwt.service;

import com.example.util.jwt.JwtProvider;
import com.example.util.jwt.repository.JwtRepository;
import com.example.util.jwt.domain.AccessToken;
import com.example.util.jwt.domain.RefreshToken;
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

    public AccessToken createTokenGateway(Map<String, Object> claims) {
        Object subject = claims.get("sub");
        String accessToken = jwtProvider.createAccessToken(subject.toString(), claims);
        return new AccessToken(accessToken);
    }

    public boolean validateToken(String token, boolean gatewayToken) {
        return jwtProvider.validateToken(token, gatewayToken);
    }

    public Map<String, String> parse(String token) {
        return jwtProvider.parse(token);
    }

    public AccessToken refreshToken(String refreshToken, String subject, Map<String, Object> claims) {
        String refreshedAccessToken = jwtProvider.refreshAccessToken(refreshToken, subject, claims);
        return new AccessToken(refreshedAccessToken);
    }
}
