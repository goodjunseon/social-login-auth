package com.junseon.auth.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    COMMON_SUCCESS(HttpStatus.OK, "COMMON_000", "Success"),
    COMMON_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON_001", "Validation failed"),
    COMMON_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "Internal server error"),

    AUTH_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "AUTH_001", "Invalid auth request"),

    SECURITY_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SECURITY_001", "Authentication is required"),
    SECURITY_FORBIDDEN(HttpStatus.FORBIDDEN, "SECURITY_002", "Access is denied");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
