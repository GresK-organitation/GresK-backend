package com.gresk.shared.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Programmatic OpenAPI 3 metadata configuration.
 *
 * <p>Provides the {@link OpenAPI} bean consumed by SpringDoc at startup.
 * Title, version, and description are the minimal required fields per the
 * OpenAPI 3.1 spec. This class is intentionally focused on metadata only;
 * security schemes and path scanning belong in separate beans as they emerge.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gresKOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GresK API")
                        .version("1.0")
                        .description("REST API for the GresK application")
                        .contact(new Contact().name("GresK Team"))
                        .license(new License().name("Private")));
    }
}
