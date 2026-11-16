package com.example.passwordchecker.controller;

import com.example.passwordchecker.dto.HealthResponse;
import com.example.passwordchecker.dto.PasswordRequest;
import com.example.passwordchecker.dto.PasswordResponse;
import com.example.passwordchecker.dto.RulesResponse;
import com.example.passwordchecker.service.PasswordAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(new HealthResponse("UP"));
    }

    @GetMapping("/password/rules")
    @Operation(summary = "Retrieve password analysis rules", description = "Returns the set of evaluation rules, length recommendations, and entropy criteria")
    public ResponseEntity<RulesResponse> getRules() {
        return ResponseEntity.ok(passwordAnalysisService.getRules());
    }

    @PostMapping("/password/analyze")
    @Operation(summary = "Analyze password strength", description = "Evaluates the submitted password and returns score (0-4), strength label, and practical suggestions")
    public ResponseEntity<PasswordResponse> analyzePassword(@Valid @RequestBody PasswordRequest request) {
        PasswordResponse response = passwordAnalysisService.analyze(request.getPassword());
        return ResponseEntity.ok(response);
    }
}
