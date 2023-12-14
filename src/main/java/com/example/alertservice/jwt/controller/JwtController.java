package com.example.alertservice.jwt.controller;

import com.example.alertservice.common.CommonApiResponse;
import com.example.alertservice.common.ErrorApiResponse;
import com.example.alertservice.jwt.service.JwtService;
import com.example.alertservice.jwt.domain.AccessToken;
import com.example.alertservice.jwt.domain.dto.CreateTokenRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/v1/jwt")
@RestController
public class JwtController {

    private static final String COMPANY = "company";

    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<AccessToken> createToken(@RequestBody CreateTokenRequestDto dto) {
        Long id = dto.getId();
        String memberType = dto.getMemberType();
        String email = dto.getEmail();
        String bno = dto.getBno();

        AccessToken token;
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);

        if (COMPANY.equals(memberType)) {
            claims.put("bno", bno);
        }

        token = jwtService.createToken(memberType, email, claims);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody AccessToken accessToken) {
        String token = accessToken.getToken();
        Boolean valid = jwtService.validateToken(token);
        return ResponseEntity.ok(valid);
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
