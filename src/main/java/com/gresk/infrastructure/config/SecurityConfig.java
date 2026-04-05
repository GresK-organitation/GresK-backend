package com.gresk.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central HTTP security configuration for Spring MVC.
 *
 * <p>Provides a single {@link SecurityFilterChain} bean that replaces Spring Security's
 * default auto-configured chain (which blocks every request with HTTP Basic).
 *
 * <p>Open paths follow the principle of least surface area: only the minimum set
 * required for documentation tooling, operational monitoring and auth endpoints
 * is whitelisted.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF: stateless REST API using JWT — no session cookies, no CSRF risk.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI docs
                        .requestMatchers(
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Auth endpoints — public by design
                        .requestMatchers(
                                "/api/v1/promoters/register",
                                "/api/v1/promoters/login"
                        ).permitAll()
                        // Profile endpoints — require an authenticated JWT principal
                        .requestMatchers("/api/v1/promoters/me").authenticated()
                        // Everything else is open for now (no JWT filter wired yet)
                        // TODO: replace anyRequest().permitAll() with authenticated()
                        //       once the JWT filter is wired and all endpoints are protected.
                        .anyRequest().permitAll()
                ).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
