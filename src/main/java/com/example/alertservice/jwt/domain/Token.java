package com.example.alertservice.jwt.domain;

import lombok.Data;

@Data
public abstract class Token {

    private final String token;

    public Token() {
        this.token = null;
    }

    public Token(String token) {
        this.token = token;
    }
}
