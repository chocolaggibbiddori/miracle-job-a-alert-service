package com.example.util.jwt.repository;

import com.example.util.jwt.domain.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class JwtRepository {

    private final Map<String, RefreshToken> refreshTokens = new HashMap<>();

    public void save(String subject, RefreshToken refreshToken) {
        refreshTokens.put(subject, refreshToken);
    }

    public Optional<RefreshToken> get(String subject) {
        return Optional.ofNullable(refreshTokens.get(subject));
    }
}
