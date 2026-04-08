package com.junseon.auth.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record EchoRequest(
        @NotBlank(message = "message must not be blank")
        String message
) {
}
