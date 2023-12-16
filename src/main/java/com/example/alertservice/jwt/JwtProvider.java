package com.example.alertservice.jwt;

import com.example.alertservice.jwt.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class JwtProvider {

    public static final String SUB_GATEWAY = "gateway";
    public static final String AUD_GATEWAY = "https://job-a.shop";
    private static final String ISSUER = "http://13.125.220.223:60200";

    private final Key key;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    public JwtProvider(String secretKey, long accessTokenValidityInSeconds, long refreshTokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    public Map<String, String> createTokens(String subject, Map<String, Object> claims) {
        String accessToken = createAccessToken(subject, claims);
        String refreshToken = createRefreshToken(subject, claims);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date expirationDate = Date.from(localDateTime.plusSeconds(accessTokenValidityInSeconds).atZone(ZoneId.systemDefault()).toInstant());

        JwtBuilder jwtBuilder = baseJwtBuilder(subject, claims);
        return jwtBuilder
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .compact();
    }

    public String createRefreshToken(String subject, Map<String, Object> claims) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date expirationDate = Date.from(localDateTime.plusSeconds(refreshTokenValidityInSeconds).atZone(ZoneId.systemDefault()).toInstant());

        JwtBuilder jwtBuilder = baseJwtBuilder(subject, claims);
        return jwtBuilder
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .compact();
    }

    private JwtBuilder baseJwtBuilder(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setIssuer(ISSUER)
                .signWith(key, SignatureAlgorithm.HS512)
                .addClaims(claims);
    }

    public boolean validateToken(String token, boolean gatewayToken) {
        try {
            if (gatewayToken) {
                Claims claims = getClaims(token);
                boolean validDefault = validateDefault(claims);

                String sub = claims.getSubject();
                boolean validSub = SUB_GATEWAY.equals(sub);

                String aud = claims.getAudience();
                boolean validAud = AUD_GATEWAY.equals(aud);

                return validDefault && validSub && validAud;
            }

            return validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean validateToken(String token) {
        Claims claims = getClaims(token);
        return validateDefault(claims);
    }

    private boolean validateDefault(Claims claims) {
        String issuer = claims.getIssuer();
        boolean validIss = Objects.equals(issuer, ISSUER);

        Date expirationDate = claims.getExpiration();
        Date now = new Date();
        boolean notExpired = now.before(expirationDate);

        return validIss && notExpired;
    }

    public String refreshAccessToken(String refreshToken, String subject, Map<String, Object> claims) {
        if (validateToken(refreshToken)) {
            return createAccessToken(subject, claims);
        }

        throw new InvalidTokenException();
    }

    private String extractSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Map<String, String> parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Map<String, String> map = new HashMap<>();
        map.put("sub", claims.getSubject());
        map.put("iss", claims.getIssuer());
        map.put("iat", claims.getIssuedAt().toString());
        map.put("exp", claims.getExpiration().toString());
        map.put("id", claims.get("id", Integer.class).toString());
        map.put("bno", claims.get("bno", String.class));
        map.put("name", claims.get("name", String.class));

        return map;
    }
}
