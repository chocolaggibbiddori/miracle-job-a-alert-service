package com.example.util.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorApiResponse extends CommonApiResponse {

    private final String code;
    private final String exception;

    @Builder
    public ErrorApiResponse(int httpStatus, String message, String code, String exception) {
        super(httpStatus, message);
        this.code = code;
        this.exception = exception;
    }
}

