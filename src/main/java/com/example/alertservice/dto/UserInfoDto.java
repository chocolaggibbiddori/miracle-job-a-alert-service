package com.example.alertservice.dto;

import lombok.Getter;

@Getter
public class UserInfoDto {

    private final String id;
    private final String realName;
    private final String name;

    public UserInfoDto(String id, String realName, String name) {
        this.id = id;
        this.realName = realName;
        this.name = name;
    }
}
