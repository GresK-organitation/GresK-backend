package com.gresk.modules.email.infrastructure.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "gresk.email.ai")
public record EmailAiProperties(
        @DefaultValue("0.85") double confidenceThreshold,
        @DefaultValue("claude-haiku-4-5-20251001") String claudeModel,
        @DefaultValue("3") int maxAttempts
) {}
