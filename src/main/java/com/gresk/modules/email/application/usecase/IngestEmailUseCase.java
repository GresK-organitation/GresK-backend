package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.application.command.IngestEmailCommand;
import com.gresk.modules.email.application.event.EmailReceivedEvent;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ingesta de un correo entrante: deduplica por message_id_external,
 * persiste en estado PENDING y publica el evento que dispara el
 * procesamiento asíncrono (pipeline de clasificación).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IngestEmailUseCase {

    private final EmailMessageRepositoryPort repository;
    private final ApplicationEventPublisher  eventPublisher;

    public EmailMessage execute(IngestEmailCommand cmd) {

        // Deduplicación: un reintento de ingesta con el mismo id externo es un no-op
        if (repository.existsByExternalMessageId(cmd.externalMessageId())) {
            log.debug("Email {} already ingested, skipping", cmd.externalMessageId());
            return repository.findByExternalMessageId(cmd.externalMessageId()).orElseThrow();
        }

        EmailMessage saved = repository.save(EmailMessage.receive(
                PromoterId.of(cmd.promoterId()),
                cmd.externalMessageId(),
                cmd.externalThreadId(),
                cmd.fromAddress(),
                cmd.fromName(),
                cmd.toAddresses(),
                cmd.subject(),
                cmd.bodyText(),
                cmd.bodyHtml(),
                cmd.rawHeadersJson(),
                cmd.receivedAt()
        ));

        eventPublisher.publishEvent(new EmailReceivedEvent(saved.getId().value()));
        return saved;
    }
}
