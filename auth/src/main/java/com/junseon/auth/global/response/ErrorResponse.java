package com.junseon.auth.global.response;

import com.junseon.auth.global.exception.ErrorCode;

import java.time.Instant;

public record ErrorResponse(
        boolean success,
        String code,
        String message,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                path,
                Instant.now()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return new ErrorResponse(
                false,
                errorCode.getCode(),
                message,
                path,
                Instant.now()
        );
    }
}
