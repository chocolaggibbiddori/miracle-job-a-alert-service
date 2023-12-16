package com.example.alertservice.jwt.controller;

import com.example.alertservice.common.CommonApiResponse;
import com.example.alertservice.common.ErrorApiResponse;
import com.example.alertservice.jwt.JwtProvider;
import com.example.alertservice.jwt.domain.AccessToken;
import com.example.alertservice.jwt.domain.dto.CreateTokenRequestDto;
import com.example.alertservice.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/v1/jwt")
@RestController
public class JwtController {

    private static final String USER = "user";
    private static final String COMPANY = "company";

    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<AccessToken> createToken(@RequestBody CreateTokenRequestDto dto) {
        Long id = dto.getId();
        String memberType = dto.getMemberType();
        String email = dto.getEmail();
        String bno = dto.getBno();
        String name = dto.getName();

        AccessToken token;
        Map<String, Object> claims = new HashMap<>();

        boolean gateway = dto.isGateway();
        if (gateway) {
            claims.put("sub", JwtProvider.SUB_GATEWAY);
            claims.put("aud", JwtProvider.AUD_GATEWAY);
            token = jwtService.createTokenGateway(claims);
        } else {
            if (COMPANY.equals(memberType)) {
                Objects.requireNonNull(bno, "Require bno");
                claims.put("bno", bno);
            } else if (USER.equals(memberType)) {
                Objects.requireNonNull(name, "Require name");
                claims.put("name", name);
            }
            claims.put("id", id);
            token = jwtService.createToken(memberType, email, claims);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody AccessToken accessToken) {
        String token = accessToken.getToken();
        Boolean valid = jwtService.validateToken(token, false);
        return ResponseEntity.ok(valid);
    }

    @PostMapping("/validate-gateway")
    public ResponseEntity<Boolean> validateGatewayToken(@RequestBody AccessToken accessToken) {
        String token = accessToken.getToken();
        Boolean valid = jwtService.validateToken(token, true);
        return ResponseEntity.ok(valid);
    }

    @GetMapping("/parse")
    public ResponseEntity<Map<String, String>> parseToken(@RequestParam String token) {
        Map<String, String> result = jwtService.parse(token);
        return ResponseEntity.ok(result);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public CommonApiResponse nullPointer(NullPointerException e) {
        int httpStatus = HttpStatus.BAD_REQUEST.value();
        String message = e.getMessage();
        String code = "400";
        String exception = e.getClass().getSimpleName();

        return new ErrorApiResponse(httpStatus, message, code, exception);
    }
}
