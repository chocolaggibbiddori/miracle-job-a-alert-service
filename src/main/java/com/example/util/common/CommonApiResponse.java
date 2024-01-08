package com.example.util.common;

import lombok.Getter;

@Getter
public class CommonApiResponse {

    private final int httpStatus;
    private final String message;

    public CommonApiResponse(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public CommonApiResponse() {
        this.httpStatus = 0;
        this.message = null;
    }
}

