package com.newbie.app.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS configuration for the application.
 *
 * <p>Allows cross-origin requests from configured origins to all {@code /api/**} endpoints.
 * The allowed origins are controlled via the {@code cors.allowed-origins} property so they
 * can be overridden per environment without changing code.</p>
 *
 * <p><b>Dev:</b> set {@code cors.allowed-origins=http://localhost:3000} (or your frontend port).<br>
 * <b>Prod:</b> restrict to the production domain only.</p>
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("${cors.allowed-origin-patterns:*}")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
