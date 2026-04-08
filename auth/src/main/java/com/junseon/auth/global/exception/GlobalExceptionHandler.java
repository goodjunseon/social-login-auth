package com.junseon.auth.global.exception;

import com.junseon.auth.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode, exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError == null ? ErrorCode.COMMON_VALIDATION_FAILED.getMessage() : fieldError.getDefaultMessage();
        ErrorResponse response = ErrorResponse.of(ErrorCode.COMMON_VALIDATION_FAILED, message, request.getRequestURI());
        return ResponseEntity.status(ErrorCode.COMMON_VALIDATION_FAILED.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_VALIDATION_FAILED,
                ErrorCode.COMMON_VALIDATION_FAILED.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ErrorCode.COMMON_VALIDATION_FAILED.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.COMMON_INTERNAL_ERROR, request.getRequestURI());
        return ResponseEntity.status(ErrorCode.COMMON_INTERNAL_ERROR.getStatus()).body(response);
    }
}
