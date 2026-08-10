package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.exception.EventNotOwnedException;
import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.port.out.EmailRiderVersionRepositoryPort;
import com.gresk.modules.email.domain.port.out.EventInfoPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/** Rider activo de un evento: la última versión registrada. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetActiveRiderUseCase {

    private final EmailRiderVersionRepositoryPort repository;
    private final EventInfoPort                   eventInfo;

    public Optional<EmailRiderVersion> execute(UUID eventId, UUID promoterId) {
        eventInfo.findEventTitle(eventId, PromoterId.of(promoterId))
                .orElseThrow(() -> new EventNotOwnedException(eventId));

        return repository.findLatestByEventId(eventId);
    }
}
