package com.example.util.jwt.domain;

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
