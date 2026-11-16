package com.example.passwordchecker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Health status payload for Kubernetes liveness/readiness probes and monitoring")
public class HealthResponse {

    @Schema(description = "Operational status", example = "UP")
    private String status;

    @Schema(description = "Current ISO-8601 timestamp", example = "2026-11-16T10:45:00Z")
    private String timestamp;

    public HealthResponse() {
        this.status = "UP";
        this.timestamp = Instant.now().toString();
    }

    public HealthResponse(String status) {
        this.status = status;
        this.timestamp = Instant.now().toString();
    }

    public HealthResponse(String status, String timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

