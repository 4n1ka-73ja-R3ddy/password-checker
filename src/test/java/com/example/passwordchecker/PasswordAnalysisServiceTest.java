package com.example.passwordchecker;

import com.example.passwordchecker.dto.PasswordResponse;
import com.example.passwordchecker.dto.RulesResponse;
import com.example.passwordchecker.service.PasswordAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Analysis Service Starter Unit Tests")
class PasswordAnalysisServiceTest {

    private PasswordAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new PasswordAnalysisService();
    }

    @Test
    @DisplayName("Short password is rated Weak or Very Weak")
    void testShortPasswordRatedWeak() {
        PasswordResponse shortResponse = service.analyze("short");
        assertEquals(0, shortResponse.getScore());
        assertEquals("Very Weak", shortResponse.getStrength());
    }

    @Test
    @DisplayName("Blank or null input handled safely")
    void testBlankAndNullPasswordHandledSafely() {
        PasswordResponse nullResponse = service.analyze(null);
        assertEquals(0, nullResponse.getScore());
        assertEquals("Very Weak", nullResponse.getStrength());

        PasswordResponse emptyResponse = service.analyze("");
        assertEquals(0, emptyResponse.getScore());
        assertEquals("Very Weak", emptyResponse.getStrength());
    }

    @Test
    @DisplayName("Rules endpoint returns guidelines")
    void testRulesEndpointReturnsGuidelines() {
        RulesResponse rulesResponse = service.getRules();
        assertNotNull(rulesResponse);
        assertNotNull(rulesResponse.getRules());
        assertFalse(rulesResponse.getRules().isEmpty());
    }
}
