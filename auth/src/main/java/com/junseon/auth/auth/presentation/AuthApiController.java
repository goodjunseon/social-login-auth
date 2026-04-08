package com.junseon.auth.auth.presentation;

import com.junseon.auth.auth.presentation.dto.EchoRequest;
import com.junseon.auth.global.exception.BaseException;
import com.junseon.auth.global.exception.ErrorCode;
import com.junseon.auth.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    @GetMapping("/ping")
    public ApiResponse<Map<String, String>> ping() {
        return ApiResponse.success(Map.of("message", "auth pong"));
    }

    @PostMapping("/echo")
    public ApiResponse<Map<String, String>> echo(@Valid @RequestBody EchoRequest request) {
        return ApiResponse.success(Map.of("message", request.message()));
    }

    @GetMapping("/business-error")
    public ApiResponse<Void> businessError() {
        throw new BaseException(ErrorCode.AUTH_INVALID_REQUEST);
    }

    @GetMapping("/error")
    public ApiResponse<Void> error() {
        throw new IllegalStateException("Unexpected failure for exception flow test");
    }
}
