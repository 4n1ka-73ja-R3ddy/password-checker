package com.example.passwordchecker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Response containing the password evaluation rules enforced by the analyzer")
public class RulesResponse {

    @Schema(description = "List of strength guidelines and heuristics")
    private List<String> rules;

    public RulesResponse() {
        this.rules = new ArrayList<>();
    }

    public RulesResponse(List<String> rules) {
        this.rules = rules != null ? rules : new ArrayList<>();
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules != null ? rules : new ArrayList<>();
    }
}

