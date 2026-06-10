package com.gresk.modules.email.infrastructure.config;

import com.gresk.modules.email.domain.port.out.AiEmailProcessorPort;
import com.gresk.modules.email.domain.port.out.LocalEmailClassifierPort;
import com.gresk.modules.email.domain.service.EmailClassificationPipeline;
import com.gresk.modules.email.domain.service.RuleBasedClassifier;
import com.gresk.modules.email.infrastructure.ai.EmailAiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EmailAiProperties.class)
public class EmailModuleConfig {

    /**
     * El pipeline se construye aquí (y no con anotaciones de estereotipo)
     * para que el dominio no dependa de la configuración de infraestructura.
     */
    @Bean
    public EmailClassificationPipeline emailClassificationPipeline(RuleBasedClassifier ruleClassifier,
                                                                   LocalEmailClassifierPort localClassifier,
                                                                   AiEmailProcessorPort aiProcessor,
                                                                   EmailAiProperties properties) {
        return new EmailClassificationPipeline(
                ruleClassifier, localClassifier, aiProcessor, properties.confidenceThreshold());
    }
}
