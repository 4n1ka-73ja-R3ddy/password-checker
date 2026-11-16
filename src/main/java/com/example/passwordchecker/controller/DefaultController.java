package com.example.passwordchecker.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Default controller to navigate incoming root and documentation requests
 * directly to the interactive Swagger UI interface.
 */
@Controller
@Hidden
public class DefaultController {

    private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);
    private static final String SWAGGER_UI_PATH = "/swagger-ui/index.html";

    /**
     * Redirects the default root path ("/") and common documentation shortcuts to Swagger UI.
     *
     * @return RedirectView targeting the Swagger UI index page
     */
    @GetMapping(value = {"/", "/swagger", "/swagger-ui", "/docs"})
    @Operation(hidden = true)
    public RedirectView redirectToSwagger() {
        logger.info("Redirecting incoming request to Swagger UI: {}", SWAGGER_UI_PATH);
        RedirectView redirectView = new RedirectView(SWAGGER_UI_PATH);
        redirectView.setContextRelative(true);
        return redirectView;
    }
}
