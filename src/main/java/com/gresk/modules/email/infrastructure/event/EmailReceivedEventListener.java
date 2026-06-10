package com.gresk.modules.email.infrastructure.event;

import com.gresk.modules.email.application.event.EmailReceivedEvent;
import com.gresk.modules.email.application.usecase.ProcessEmailUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Dispatch asíncrono del pipeline: se ejecuta tras el commit del ingest
 * para garantizar que el correo ya es visible en base de datos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailReceivedEventListener {

    private final ProcessEmailUseCase processEmailUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmailReceived(EmailReceivedEvent event) {
        try {
            processEmailUseCase.execute(event.emailId());
        } catch (Exception e) {
            log.error("Async processing failed for email {}: {}", event.emailId(), e.getMessage());
        }
    }
}
