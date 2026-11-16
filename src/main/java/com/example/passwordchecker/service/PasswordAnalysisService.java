package com.example.passwordchecker.service;

import com.example.passwordchecker.dto.PasswordResponse;
import com.example.passwordchecker.dto.RulesResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class PasswordAnalysisService {

    // Locally stored demonstration list of commonly used / easy-to-guess passwords
    private static final Set<String> COMMON_PASSWORDS = Set.of(
            "password", "123456", "12345678", "1234", "12345", "123456789",
            "qwerty", "admin", "welcome", "letmein", "monkey", "dragon",
            "password123", "master", "football", "iloveyou", "abc123", "default"
    );


    // Repetition regex pattern: 3 or more identical characters consecutively (e.g., 'aaa', '111')
    private static final Pattern REPEATING_PATTERN = Pattern.compile("(.)\\1{2,}");

    private boolean checkSequences(String input) {
        String lower = input.toLowerCase();
        // Keyboard sequences (3+ chars)
        String[] keyboardPatterns = {
                "qwe", "wer", "ert", "rty", "tyu", "yui", "uio", "iop",
                "asd", "sdf", "dfg", "fgh", "ghj", "hjk", "jkl",
                "zxc", "xcv", "cvb", "vbn", "bnm"
        };
        for (String kp : keyboardPatterns) {
            if (lower.contains(kp)) {
                return true;
            }
        }

        // Ascending or descending consecutive characters (digits or letters, e.g. '123', 'abc', '321')
        for (int i = 0; i < lower.length() - 2; i++) {
            char c1 = lower.charAt(i);
            char c2 = lower.charAt(i + 1);
            char c3 = lower.charAt(i + 2);

            if (Character.isLetterOrDigit(c1) && Character.isLetterOrDigit(c2) && Character.isLetterOrDigit(c3)) {
                // Ascending consecutive sequence
                if (c2 == c1 + 1 && c3 == c2 + 1) {
                    return true;
                }
                // Descending consecutive sequence
                if (c2 == c1 - 1 && c3 == c2 - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public PasswordResponse analyze(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new PasswordResponse(0, "Very Weak", List.of("Password must not be blank or contain only whitespace."));
        }

        String lowerPwd = password.toLowerCase();
        List<String> suggestions = new ArrayList<>();

        // 1. Check for common/easy-to-guess passwords from local demonstration list
        if (COMMON_PASSWORDS.contains(lowerPwd)) {
            suggestions.add("Avoid common words and predictable sequences.");
            suggestions.add("Use a longer password or passphrase.");
            suggestions.add("Consider adding more variety if appropriate.");
            return new PasswordResponse(0, "Very Weak", suggestions);
        }

        // 2. Length check: encourage at least 12 characters and preferably longer passphrases
        int length = password.length();
        if (length < 8) {
            suggestions.add("Use a longer password or passphrase (at least 12 characters is recommended).");
        } else if (length < 12) {
            suggestions.add("Use a longer password or passphrase (at least 12 characters is recommended).");
        }

        // 3. Obvious sequences and repetitions
        boolean hasSequences = checkSequences(password);
        if (hasSequences) {
            suggestions.add("Avoid common words and predictable sequences.");
        }

        boolean hasRepetitions = REPEATING_PATTERN.matcher(password).find();
        if (hasRepetitions) {
            suggestions.add("Avoid repeating characters consecutively.");
        }

        // 4. Character variety (supporting signal, not mandatory rule)
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        int varietyCount = 0;
        if (hasLower) varietyCount++;
        if (hasUpper) varietyCount++;
        if (hasDigit) varietyCount++;
        if (hasSpecial) varietyCount++;

        if (varietyCount < 3) {
            suggestions.add("Consider adding more variety if appropriate.");
            if (!hasUpper) {
                suggestions.add("Include uppercase letters (A-Z) to improve complexity.");
            }
            if (!hasDigit) {
                suggestions.add("Include numeric digits (0-9) to strengthen entropy.");
            }
            if (!hasSpecial) {
                suggestions.add("Include special symbols (e.g., !@#$%^&*) for added protection.");
            }
        }

        // 5. Score calculation (0 to 4)
        int score;
        if (length >= 16 && varietyCount >= 3 && !hasSequences && !hasRepetitions) {
            score = 4;
        } else if (length >= 12 && varietyCount >= 3 && !hasSequences && !hasRepetitions) {
            score = 3;
        } else if (length >= 8 && varietyCount >= 2) {
            score = 2;
        } else if (length >= 8) {
            score = 1;
        } else {
            score = 0;
        }

        // 6. Categorical strength rating
        String strength;
        if (score == 4) {
            strength = "Very Strong";
        } else if (score == 3) {
            strength = "Strong";
        } else if (score == 2) {
            strength = (hasSequences || hasRepetitions || length < 12) ? "Weak" : "Moderate";
        } else if (score == 1) {
            strength = "Weak";
        } else {
            strength = "Very Weak";
        }

        if (suggestions.isEmpty() && score >= 3) {
            suggestions.add("Great password! Maintain good password hygiene and do not reuse it across accounts.");
        }

        return new PasswordResponse(score, strength, suggestions);
    }

    public RulesResponse getRules() {
        return new RulesResponse(List.of(
                "Rule 1 - Length: Passwords should be at least 12 characters long. Longer passphrases (16+ chars) offer superior resistance.",
                "Rule 2 - Common Passwords: Passwords found in commonly used lists (e.g., 'password', '123456') are rated Very Weak.",
                "Rule 3 - Predictable Sequences: Avoid sequential patterns (e.g., '1234', 'abcd') and repeated characters (e.g., 'aaa').",
                "Rule 4 - Variety: Use a balanced mix of lowercase, uppercase, numbers, and symbols as a supporting signal.",
                "Rule 5 - Input Bounds: Blank input is rejected; maximum allowed input length is 128 characters for security."
        ));
    }
}
