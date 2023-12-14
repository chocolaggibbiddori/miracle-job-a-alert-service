package com.example.alertservice.jwt.domain;

import lombok.Getter;

@Getter
abstract class Token {

    private final String token;

    Token(String token) {
        this.token = token;
    }
}
