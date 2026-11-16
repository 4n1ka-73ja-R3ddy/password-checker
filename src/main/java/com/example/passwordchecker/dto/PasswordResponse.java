package com.example.passwordchecker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Response payload containing the password strength score, rating label, and suggestions")
public class PasswordResponse {

    @Schema(description = "Numerical score from 0 (very weak) to 4 (very strong)", example = "2")
    private int score;

    @Schema(description = "Categorical strength rating", example = "Weak")
    private String strength;

    @Schema(description = "List of actionable improvement suggestions based on detected weaknesses")
    private List<String> suggestions;

    public PasswordResponse() {
        this.suggestions = new ArrayList<>();
    }

    public PasswordResponse(int score, String strength, List<String> suggestions) {
        this.score = score;
        this.strength = strength;
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }
}

