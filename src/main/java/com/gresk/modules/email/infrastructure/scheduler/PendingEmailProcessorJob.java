package com.gresk.modules.email.infrastructure.scheduler;

import com.gresk.modules.email.application.event.EmailReceivedEvent;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.email.infrastructure.ai.EmailAiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Reintentos del pipeline con backoff exponencial (30s → 60s → 120s...).
 * Los emails FAILED que agotaron los reintentos pasan a DEAD_LETTER.
 * El reintento reutiliza el mismo flujo asíncrono que el ingest: se publica
 * EmailReceivedEvent y el listener ejecuta ProcessEmailUseCase tras el commit.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PendingEmailProcessorJob {

    private static final long BASE_BACKOFF_SECONDS = 30L;

    private final EmailMessageRepositoryPort emailRepository;
    private final ApplicationEventPublisher  eventPublisher;
    private final EmailAiProperties          properties;

    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void retryFailedEmails() {
        int maxAttempts = properties.maxAttempts();

        emailRepository.findFailedWithAttemptsLessThan(maxAttempts).stream()
                .filter(this::isReadyForRetry)
                .forEach(email -> {
                    log.info("Reintentando email {} (intento {})",
                            email.getId(), email.getProcessingAttempts() + 1);
                    eventPublisher.publishEvent(new EmailReceivedEvent(email.getId().value()));
                });

        // Safety net: emails que agotaron reintentos → DEAD_LETTER
        emailRepository.findFailedWithAttemptsAtLeast(maxAttempts)
                .forEach(email -> {
                    email.markDeadLetter();
                    emailRepository.save(email);
                    log.error("Email {} marcado como DEAD_LETTER tras {} intentos",
                            email.getId(), email.getProcessingAttempts());
                });
    }

    /** Backoff exponencial: el intento n espera 30 * 2^n segundos desde el último fallo. */
    private boolean isReadyForRetry(EmailMessage email) {
        if (email.getLastAttemptAt() == null) {
            return true;
        }
        long backoffSeconds = BASE_BACKOFF_SECONDS * (long) Math.pow(2, email.getProcessingAttempts());
        return Instant.now().isAfter(email.getLastAttemptAt().plusSeconds(backoffSeconds));
    }
}
