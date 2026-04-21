package com.gresk.modules.user.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spotify")
@Getter
@Setter
public class SpotifyConfig {
    private String clientId;
    private String clientSecret;
}