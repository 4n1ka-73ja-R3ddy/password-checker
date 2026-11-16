package com.example.passwordchecker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload containing the password to analyze")
public class PasswordRequest {

    @Schema(description = "Password string to evaluate", example = "hello123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password must not be blank")
    @Size(max = 128, message = "Password must not exceed 128 characters")
    private String password;

    public PasswordRequest() {
    }

    public PasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

