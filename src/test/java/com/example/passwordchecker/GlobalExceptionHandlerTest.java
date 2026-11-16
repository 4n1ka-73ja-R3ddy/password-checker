package com.example.passwordchecker;

import com.example.passwordchecker.controller.PasswordController;
import com.example.passwordchecker.dto.PasswordResponse;
import com.example.passwordchecker.exception.GlobalExceptionHandler;
import com.example.passwordchecker.exception.InvalidPasswordException;
import com.example.passwordchecker.service.PasswordAnalysisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Global Exception Handler & API Validation Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordAnalysisService passwordAnalysisService;

    @Test
    @DisplayName("Blank password in request body returns 400 Bad Request with standardized ErrorResponse")
    void testBlankPasswordValidationFailure() throws Exception {
        String invalidJson = "{\"password\":\"\"}";

        mockMvc.perform(post("/api/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.path").value("/api/password/analyze"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.message", containsString("Password must not be blank")))
                .andExpect(jsonPath("$.details", hasItem(containsString("password"))));
    }

    @Test
    @DisplayName("Malformed JSON payload returns 400 Bad Request with descriptive message")
    void testMalformedJsonPayload() throws Exception {
        String brokenJson = "{\"password\":";

        mockMvc.perform(post("/api/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(brokenJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/password/analyze"))
                .andExpect(jsonPath("$.message", containsString("Malformed JSON request body")));
    }

    @Test
    @DisplayName("Unsupported HTTP method (GET on POST endpoint) returns 405 Method Not Allowed")
    void testMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/api/password/analyze"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.path").value("/api/password/analyze"))
                .andExpect(jsonPath("$.message", containsString("Request method 'GET' is not supported")));
    }

    @Test
    @DisplayName("Unsupported Media Type (e.g. text/plain) returns 415 Unsupported Media Type")
    void testUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/password/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello123"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.path").value("/api/password/analyze"));
    }

    @Test
    @DisplayName("IllegalArgumentException from service returns 400 Bad Request")
    void testIllegalArgumentExceptionHandling() throws Exception {
        Mockito.when(passwordAnalysisService.analyze(anyString()))
                .thenThrow(new IllegalArgumentException("Custom illegal argument rejection"));

        mockMvc.perform(post("/api/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"ValidDummy123!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Custom illegal argument rejection"));
    }

    @Test
    @DisplayName("InvalidPasswordException from service returns 400 Bad Request with domain error")
    void testInvalidPasswordExceptionHandling() throws Exception {
        Mockito.when(passwordAnalysisService.analyze(anyString()))
                .thenThrow(new InvalidPasswordException("Password contains prohibited character sequence"));

        mockMvc.perform(post("/api/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"Prohibited123!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Password"))
                .andExpect(jsonPath("$.message").value("Password contains prohibited character sequence"));
    }

    @Test
    @DisplayName("Valid password analysis request succeeds with 200 OK")
    void testValidPasswordAnalysisSucceeds() throws Exception {
        Mockito.when(passwordAnalysisService.analyze("hello123"))
                .thenReturn(new PasswordResponse(2, "Weak", List.of("Avoid common words and predictable sequences.")));

        mockMvc.perform(post("/api/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"hello123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(2))
                .andExpect(jsonPath("$.strength").value("Weak"))
                .andExpect(jsonPath("$.suggestions", hasItem(containsString("predictable sequences"))));
    }
}
