package com.gresk.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Central HTTP security configuration for Spring WebFlux.
 *
 * <p>Provides a single {@link SecurityWebFilterChain} bean that replaces Spring Security's
 * default auto-configured chain (which blocks every request with HTTP Basic).
 *
 * <p>Open paths follow the principle of least surface area: only the minimum set
 * required for documentation tooling, operational monitoring and auth endpoints
 * is whitelisted. Endpoints under /me require a valid authenticated principal.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF: stateless REST API using JWT — no session cookies, no CSRF risk.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Swagger / OpenAPI docs
                        .pathMatchers(
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Auth endpoints — public by design
                        .pathMatchers(
                                "/api/v1/promoters/register",
                                "/api/v1/promoters/login"
                        ).permitAll()
                        // Profile endpoints — require an authenticated JWT principal
                        .pathMatchers("/api/v1/promoters/me").authenticated()
                        // Everything else is open for now (no other domain endpoints yet)
                        // TODO: replace anyExchange().permitAll() with authenticated() globally
                        //       once the JWT filter is wired and all endpoints are protected.
                        .anyExchange().permitAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
