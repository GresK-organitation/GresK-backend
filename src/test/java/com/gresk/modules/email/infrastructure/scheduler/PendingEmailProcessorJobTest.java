package com.gresk.modules.email.infrastructure.scheduler;

import com.gresk.modules.email.application.event.EmailReceivedEvent;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.domain.model.ProcessingStatus;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.email.infrastructure.ai.EmailAiProperties;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendingEmailProcessorJobTest {

    private static final int MAX_ATTEMPTS = 3;

    @Mock private EmailMessageRepositoryPort repository;
    @Mock private ApplicationEventPublisher  eventPublisher;

    private PendingEmailProcessorJob job;

    @BeforeEach
    void setUp() {
        job = new PendingEmailProcessorJob(repository, eventPublisher,
                new EmailAiProperties(0.85, "claude-haiku-4-5-20251001", MAX_ATTEMPTS));
    }

    @Test
    void noReintentaUnEmailAntesDeQueExpireSuBackoff() {
        // 2 intentos → backoff de 30 * 2^2 = 120s; el último fallo fue hace 10s
        EmailMessage email = failedEmail(2, Instant.now().minusSeconds(10));
        when(repository.findFailedWithAttemptsLessThan(MAX_ATTEMPTS)).thenReturn(List.of(email));
        when(repository.findFailedWithAttemptsAtLeast(MAX_ATTEMPTS)).thenReturn(List.of());

        job.retryFailedEmails();

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void reintentaUnEmailCuandoSuBackoffYaExpiro() {
        // 1 intento → backoff de 60s; el último fallo fue hace 5 minutos
        EmailMessage email = failedEmail(1, Instant.now().minusSeconds(300));
        when(repository.findFailedWithAttemptsLessThan(MAX_ATTEMPTS)).thenReturn(List.of(email));
        when(repository.findFailedWithAttemptsAtLeast(MAX_ATTEMPTS)).thenReturn(List.of());

        job.retryFailedEmails();

        verify(eventPublisher).publishEvent(any(EmailReceivedEvent.class));
    }

    @Test
    void unEmailConLosIntentosAgotadosPasaADeadLetterEnLaSiguienteEjecucion() {
        EmailMessage exhausted = failedEmail(MAX_ATTEMPTS, Instant.now().minusSeconds(600));
        when(repository.findFailedWithAttemptsLessThan(MAX_ATTEMPTS)).thenReturn(List.of());
        when(repository.findFailedWithAttemptsAtLeast(MAX_ATTEMPTS)).thenReturn(List.of(exhausted));

        job.retryFailedEmails();

        assertEquals(ProcessingStatus.DEAD_LETTER, exhausted.getProcessingStatus());
        verify(repository).save(exhausted);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void unaEjecucionSinCandidatosNoHaceNada() {
        when(repository.findFailedWithAttemptsLessThan(anyInt())).thenReturn(List.of());
        when(repository.findFailedWithAttemptsAtLeast(anyInt())).thenReturn(List.of());

        job.retryFailedEmails();

        verify(eventPublisher, never()).publishEvent(any());
        verify(repository, never()).save(any());
    }

    private EmailMessage failedEmail(int attempts, Instant lastAttemptAt) {
        return EmailMessage.reconstitute(
                EmailMessageId.generate(),
                PromoterId.of(UUID.randomUUID()),
                null,
                "msg-" + UUID.randomUUID(), "thread-1",
                "promotor@sala.com", null, List.of(),
                "Asunto", "Cuerpo", null, null,
                null, null,
                ProcessingStatus.FAILED, attempts,
                lastAttemptAt, null,
                Instant.now().minusSeconds(3600), Instant.now().minusSeconds(3600));
    }
}
