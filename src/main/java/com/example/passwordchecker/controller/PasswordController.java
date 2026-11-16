package com.example.passwordchecker.controller;

import com.example.passwordchecker.dto.ErrorResponse;
import com.example.passwordchecker.dto.HealthResponse;
import com.example.passwordchecker.dto.PasswordRequest;
import com.example.passwordchecker.dto.PasswordResponse;
import com.example.passwordchecker.dto.RulesResponse;
import com.example.passwordchecker.service.PasswordAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Password Strength Checker API", description = "Endpoints for password analysis, rules documentation, and system health checks")
public class PasswordController {

    private final PasswordAnalysisService passwordAnalysisService;

    public PasswordController(PasswordAnalysisService passwordAnalysisService) {
        this.passwordAnalysisService = passwordAnalysisService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint", description = "Used by Kubernetes liveness and readiness probes to confirm API availability")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is up and running",
                    content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    })
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(new HealthResponse("UP"));
    }

    @GetMapping("/password/rules")
    @Operation(summary = "Retrieve password analysis rules", description = "Returns the set of evaluation rules, length recommendations, and entropy criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rules retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RulesResponse.class)))
    })
    public ResponseEntity<RulesResponse> getRules() {
        return ResponseEntity.ok(passwordAnalysisService.getRules());
    }

    @PostMapping("/password/analyze")
    @Operation(summary = "Analyze password strength", description = "Evaluates the submitted password and returns score (0-4), strength label, and practical suggestions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password analyzed successfully",
                    content = @Content(schema = @Schema(implementation = PasswordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or validation constraint violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PasswordResponse> analyzePassword(@Valid @RequestBody PasswordRequest request) {
        PasswordResponse response = passwordAnalysisService.analyze(request.getPassword());
        return ResponseEntity.ok(response);
    }
}
