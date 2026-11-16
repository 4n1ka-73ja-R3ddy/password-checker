package com.example.passwordchecker.service;

import com.example.passwordchecker.dto.PasswordResponse;
import com.example.passwordchecker.dto.RulesResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordAnalysisService {

    public PasswordResponse analyze(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new PasswordResponse(0, "Very Weak", List.of("Password must not be blank or contain only whitespace."));
        }

        List<String> suggestions = new ArrayList<>();
        int length = password.length();

        int score;
        String strength;

        if (length < 8) {
            score = 0;
            strength = "Very Weak";
            suggestions.add("Use a longer password or passphrase (at least 12 characters is recommended).");
        } else if (length < 12) {
            score = 1;
            strength = "Weak";
            suggestions.add("Use a longer password or passphrase (at least 12 characters is recommended).");
        } else if (length < 16) {
            score = 2;
            strength = "Moderate";
            suggestions.add("Consider adding more variety if appropriate.");
        } else {
            score = 3;
            strength = "Strong";
            suggestions.add("Great password! Maintain good password hygiene and do not reuse it across accounts.");
        }

        return new PasswordResponse(score, strength, suggestions);
    }

    public RulesResponse getRules() {
        return new RulesResponse(List.of(
                "Rule 1 - Length: Passwords should be at least 12 characters long. Longer passphrases (16+ chars) offer superior resistance.",
                "Rule 2 - Common Passwords: Avoid commonly used words.",
                "Rule 3 - Predictable Sequences: Avoid sequential patterns.",
                "Rule 4 - Input Bounds: Blank input is rejected; maximum allowed input length is 128 characters for security."
        ));
    }
}
