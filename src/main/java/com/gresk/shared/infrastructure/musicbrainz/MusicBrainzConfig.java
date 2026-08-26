package com.gresk.shared.infrastructure.musicbrainz;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "musicbrainz")
@Getter
@Setter
public class MusicBrainzConfig {
    private String apiUrl;
    private String userAgent;
}
