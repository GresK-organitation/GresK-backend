package com.gresk.shared.infrastructure.bandsintown;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bandsintown")
public class BandsintownConfig {
    private String appId;
    private String apiUrl;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
}
