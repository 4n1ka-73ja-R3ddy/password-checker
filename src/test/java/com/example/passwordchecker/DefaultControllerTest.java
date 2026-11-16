package com.example.passwordchecker;

import com.example.passwordchecker.controller.DefaultController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DefaultController.class)
@DisplayName("Default Controller Navigation Tests")
class DefaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Root URL ('/') redirects to Swagger UI index")
    void testRootRedirectsToSwaggerUi() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"))
                .andExpect(header().string("Location", "/swagger-ui/index.html"));
    }

    @Test
    @DisplayName("Shortcut '/swagger' redirects to Swagger UI index")
    void testSwaggerShortcutRedirectsToSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    @DisplayName("Shortcut '/swagger-ui' redirects to Swagger UI index")
    void testSwaggerUiShortcutRedirectsToSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    @DisplayName("Shortcut '/docs' redirects to Swagger UI index")
    void testDocsShortcutRedirectsToSwaggerUi() throws Exception {
        mockMvc.perform(get("/docs"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }
}
