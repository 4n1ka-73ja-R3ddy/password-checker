package com.example.passwordchecker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Standardized error response payload")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2026-11-16T12:00:00Z")
    private String timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP error title", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "Password must not be blank")
    private String message;

    @Schema(description = "API endpoint path where the error occurred", example = "/api/password/analyze")
    private String path;

    @Schema(description = "List of specific field validation errors, if applicable")
    private List<String> details;

    public ErrorResponse() {
        this.timestamp = Instant.now().toString();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path, List<String> details) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
