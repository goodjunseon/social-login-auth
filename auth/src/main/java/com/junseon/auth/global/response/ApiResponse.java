package com.junseon.auth.global.response;

import com.junseon.auth.global.exception.ErrorCode;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                ErrorCode.COMMON_SUCCESS.getCode(),
                ErrorCode.COMMON_SUCCESS.getMessage(),
                data
        );
    }

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }
}
