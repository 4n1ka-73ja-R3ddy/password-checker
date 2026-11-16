package com.example.passwordchecker;

import com.example.passwordchecker.dto.PasswordResponse;
import com.example.passwordchecker.dto.RulesResponse;
import com.example.passwordchecker.service.PasswordAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Analysis Service Unit Tests")
class PasswordAnalysisServiceTest {

    private PasswordAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new PasswordAnalysisService();
    }

    @Test
    @DisplayName("Test 1: Short or predictable password is rated Weak or Very Weak")
    void testShortOrPredictablePasswordRatedWeak() {
        // Test common dictionary password
        PasswordResponse commonResponse = service.analyze("password");
        assertEquals(0, commonResponse.getScore(), "Common password should receive lowest score (0)");
        assertEquals("Very Weak", commonResponse.getStrength(), "Common password strength should be 'Very Weak'");
        assertTrue(commonResponse.getSuggestions().stream().anyMatch(s -> s.contains("common words")),
                "Suggestions should alert about common words");

        // Test predictable password with sequence (illustrative example from specification)
        PasswordResponse seqResponse = service.analyze("hello123");
        assertEquals(2, seqResponse.getScore(), "Score should match 2 for 'hello123'");
        assertEquals("Weak", seqResponse.getStrength(), "Predictable pattern should be rated 'Weak'");
        assertTrue(seqResponse.getSuggestions().stream().anyMatch(s -> s.contains("longer password")),
                "Should suggest longer password");
        assertTrue(seqResponse.getSuggestions().stream().anyMatch(s -> s.contains("predictable sequences")),
                "Should alert about predictable sequence '123'");
    }

    @Test
    @DisplayName("Test 2: Longer, diverse password receives Strong or Very Strong rating")
    void testLongerLessPredictablePasswordReceivesStrongRating() {
        // High entropy passphrase with mixed types
        PasswordResponse strongResponse = service.analyze("Super$ecure#Passphrase2026!");
        assertTrue(strongResponse.getScore() >= 3, "High-entropy long password should score 3 or 4");
        assertTrue(strongResponse.getStrength().equals("Strong") || strongResponse.getStrength().equals("Very Strong"),
                "Strength should be Strong or Very Strong");
        assertNotNull(strongResponse.getSuggestions());

        // 16+ chars with diverse characters
        PasswordResponse veryStrongResponse = service.analyze("K9#mQ2$vL8!pZ4@wX7");
        assertEquals(4, veryStrongResponse.getScore(), "16+ char diverse password should score 4");
        assertEquals("Very Strong", veryStrongResponse.getStrength(), "Strength should be 'Very Strong'");
    }

    @Test
    @DisplayName("Test 3: Suggestion list changes dynamically based on detected weaknesses")
    void testSuggestionsChangeBasedOnDetectedWeakness() {
        // Case A: Missing variety (only lowercase letters)
        PasswordResponse missingVariety = service.analyze("onlylowercasecharacters");
        assertTrue(missingVariety.getSuggestions().stream().anyMatch(s -> s.contains("more variety")),
                "Should suggest adding variety when only lowercase is used");

        // Case B: Repetitive characters
        PasswordResponse repeatedChars = service.analyze("SecurePassword11111");
        assertTrue(repeatedChars.getSuggestions().stream().anyMatch(s -> s.contains("repeating characters")),
                "Should advise against repeating characters");

        // Case C: Too short (under 8 chars)
        PasswordResponse shortPwd = service.analyze("Ab1!");
        assertTrue(shortPwd.getSuggestions().stream().anyMatch(s -> s.contains("at least 12 characters")),
                "Should advise increasing length to at least 12 characters");
    }

    @Test
    @DisplayName("Test 4: Blank, null, or whitespace-only input returns safe validation response")
    void testBlankAndNullPasswordHandledSafely() {
        PasswordResponse nullResponse = service.analyze(null);
        assertEquals(0, nullResponse.getScore());
        assertEquals("Very Weak", nullResponse.getStrength());
        assertFalse(nullResponse.getSuggestions().isEmpty());

        PasswordResponse emptyResponse = service.analyze("");
        assertEquals(0, emptyResponse.getScore());
        assertEquals("Very Weak", emptyResponse.getStrength());

        PasswordResponse whitespaceResponse = service.analyze("   ");
        assertEquals(0, whitespaceResponse.getScore());
        assertEquals("Very Weak", whitespaceResponse.getStrength());
    }

    @Test
    @DisplayName("Test 5: Rules endpoint returns well-formed guidelines")
    void testRulesEndpointReturnsGuidelines() {
        RulesResponse rulesResponse = service.getRules();
        assertNotNull(rulesResponse);
        assertNotNull(rulesResponse.getRules());
        assertTrue(rulesResponse.getRules().size() >= 4, "Should provide at least 4 clear rules");
    }
}

