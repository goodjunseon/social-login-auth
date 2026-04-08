package com.junseon.auth.auth.presentation;

import com.junseon.auth.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SecureApiController {

    @GetMapping("/secure/ping")
    public ApiResponse<Map<String, String>> securePing() {
        return ApiResponse.success(Map.of("message", "secure pong"));
    }

    @GetMapping("/admin/ping")
    public ApiResponse<Map<String, String>> adminPing() {
        return ApiResponse.success(Map.of("message", "admin pong"));
    }
}
