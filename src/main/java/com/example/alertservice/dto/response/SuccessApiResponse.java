package com.example.alertservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessApiResponse<T> extends CommonApiResponse {

    private final T data;

    @Builder
    public SuccessApiResponse(int httpStatus, String message, T data) {
        super(httpStatus, message);
        this.data = data;
    }
    @Builder
    public SuccessApiResponse(int httpStatus, String message) {
        super(httpStatus, message);
        this.data = null;
    }

    public SuccessApiResponse() {
        this.data = null;
    }
}
