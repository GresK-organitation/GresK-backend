package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.application.command.IngestEmailCommand;
import com.gresk.modules.email.application.event.EmailReceivedEvent;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestEmailUseCaseTest {

    @Mock private EmailMessageRepositoryPort repository;
    @Mock private ApplicationEventPublisher  eventPublisher;

    @InjectMocks
    private IngestEmailUseCase useCase;

    private static final String EXTERNAL_ID = "gmail-msg-12345";

    @Test
    void primerIngest_persisteYPublicaElEventoDeProcesamiento() {
        when(repository.existsByExternalMessageId(EXTERNAL_ID)).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmailMessage result = useCase.execute(command());

        assertEquals(EXTERNAL_ID, result.getExternalMessageId());
        verify(repository).save(any(EmailMessage.class));
        verify(eventPublisher).publishEvent(any(EmailReceivedEvent.class));
    }

    @Test
    void segundoIngestConMismoIdExterno_noPersisteNada() {
        EmailMessage existing = EmailMessage.receive(
                PromoterId.of(UUID.randomUUID()), EXTERNAL_ID, "thread-1",
                "promotor@sala.com", null, List.of(), "Asunto", "Cuerpo",
                null, null, Instant.now());
        when(repository.existsByExternalMessageId(EXTERNAL_ID)).thenReturn(true);
        when(repository.findByExternalMessageId(EXTERNAL_ID)).thenReturn(Optional.of(existing));

        EmailMessage result = useCase.execute(command());

        assertEquals(existing, result);
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    private IngestEmailCommand command() {
        return new IngestEmailCommand(
                UUID.randomUUID(), EXTERNAL_ID, "thread-1",
                "promotor@sala.com", "Sala Apolo",
                List.of("promotora@gresk.com"),
                "Asunto", "Cuerpo", null, null,
                Instant.now());
    }
}
