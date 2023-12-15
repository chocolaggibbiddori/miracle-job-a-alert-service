package com.example.alertservice.jwt.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class AccessToken extends Token {

    public AccessToken(String token) {
        super(token);
    }
}
