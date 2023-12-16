package com.example.alertservice.jwt.domain.dto;

import lombok.Data;

@Data
public class CreateTokenRequestDto {

    private final Long id;
    private final String email;
    private final String memberType;
    private final String bno;
    private final String name;
    private final boolean gateway;

    public CreateTokenRequestDto() {
        this.id = null;
        this.email = null;
        this.memberType = null;
        this.bno = null;
        this.name = null;
        this.gateway = false;
    }
}
