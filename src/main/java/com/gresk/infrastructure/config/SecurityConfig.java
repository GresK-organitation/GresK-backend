package com.gresk.infrastructure.config;

import com.gresk.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final List<String> allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${gresk.cors.allowed-origins}") List<String> allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Gmail: push de Pub/Sub (token compartido) y callback OAuth (state anti-CSRF)
                        .requestMatchers("/api/v1/email/gmail/webhook").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/email/gmail/callback").permitAll()
                        // Calendar sync: callbacks OAuth (state anti-CSRF) y webhooks push de Google/Outlook
                        // (verificados por clientStateSecret propio de cada cuenta, no un token compartido)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/agenda/calendar-sync/*/callback").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/agenda/calendar-sync/webhook/**").permitAll()
                        // Firma digital: webhook del proveedor (Signaturit/DocuSign/stub), protegido por token compartido
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/webhooks/signature/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/events").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/events/last-minute").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/events/{id}").permitAll()
                        // Tendencias: crónicas publicadas y fichas de datos propios son públicas;
                        // /api/v1/tendencias/admin/** queda fuera a propósito (requiere ADMIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/tendencias/chronicles").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/tendencias/artists/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/tendencias/venues/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/tendencias/genres/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/tendencias/events/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://*.vercel.app"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type", "X-Total-Count"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}