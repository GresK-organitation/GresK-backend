package com.gresk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central HTTP security configuration.
 *
 * <p>Provides a single {@link SecurityFilterChain} bean that replaces Spring Security's
 * default auto-configured chain (which blocks every request with HTTP Basic).
 *
 * <p>Open paths follow the principle of least surface area: only the minimum set
 * required for documentation tooling and operational monitoring is whitelisted.
 * All other paths are left open for now (permitAll) because no domain endpoints
 * exist yet; this must be revisited when user-facing API endpoints are added.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
        // Actuator
        "/actuator/**",
        // SpringDoc – raw OpenAPI JSON/YAML spec
        "/v3/api-docs/**",
        // SpringDoc – Swagger UI static resources (JS, CSS, HTML fragments)
        "/swagger-ui/**",
        // Redirect entry point (Spring MVC maps this → /swagger-ui/index.html)
        "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for a stateless REST API.
            // CSRF protection is only meaningful for browser-based session flows.
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                // TODO: replace with authenticated() when domain endpoints are added
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
