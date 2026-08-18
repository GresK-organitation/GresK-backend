package com.gresk.modules.tendencias.chronicle.infrastructure.config;

import com.gresk.modules.tendencias.chronicle.infrastructure.rss.TendenciasFeedProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TendenciasFeedProperties.class)
public class TendenciasChronicleModuleConfig {
}
