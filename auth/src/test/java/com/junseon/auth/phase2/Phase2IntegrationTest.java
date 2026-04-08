package com.junseon.auth.phase2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Phase2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authEndpointShouldReturnCommonSuccessEnvelope() throws Exception {
        mockMvc.perform(get("/api/v1/auth/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("COMMON_000"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.message").value("auth pong"));
    }

    @Test
    void validationFailureShouldUseCommonErrorFormat() throws Exception {
        mockMvc.perform(post("/api/v1/auth/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON_001"))
                .andExpect(jsonPath("$.message").value("message must not be blank"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/echo"));
    }

    @Test
    void businessExceptionShouldUseErrorCodeFormat() throws Exception {
        mockMvc.perform(get("/api/v1/auth/business-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_001"))
                .andExpect(jsonPath("$.message").value("Invalid auth request"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/business-error"));
    }

    @Test
    void unexpectedExceptionShouldMapToInternalErrorCode() throws Exception {
        mockMvc.perform(get("/api/v1/auth/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON_999"))
                .andExpect(jsonPath("$.message").value("Internal server error"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/error"));
    }

    @Test
    void actuatorHealthShouldBePublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void protectedEndpointShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/secure/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("SECURITY_001"))
                .andExpect(jsonPath("$.message").value("Authentication is required"))
                .andExpect(jsonPath("$.path").value("/api/v1/secure/ping"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpointShouldReturn403ForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/ping"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("SECURITY_002"))
                .andExpect(jsonPath("$.message").value("Access is denied"))
                .andExpect(jsonPath("$.path").value("/api/v1/admin/ping"));
    }
}
