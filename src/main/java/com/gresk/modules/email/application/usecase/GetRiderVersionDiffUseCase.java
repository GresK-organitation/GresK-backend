package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.exception.EventNotOwnedException;
import com.gresk.modules.email.domain.exception.RiderVersionNotFoundException;
import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.model.EmailRiderVersionId;
import com.gresk.modules.email.domain.port.out.EmailRiderVersionRepositoryPort;
import com.gresk.modules.email.domain.port.out.EventInfoPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Diff de una versión de rider respecto a la anterior (almacenado al crearla). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetRiderVersionDiffUseCase {

    private final EmailRiderVersionRepositoryPort repository;
    private final EventInfoPort                   eventInfo;

    public EmailRiderVersion execute(UUID eventId, UUID versionId, UUID promoterId) {
        eventInfo.findEventTitle(eventId, PromoterId.of(promoterId))
                .orElseThrow(() -> new EventNotOwnedException(eventId));

        EmailRiderVersionId id = EmailRiderVersionId.of(versionId);
        EmailRiderVersion version = repository.findById(id)
                .orElseThrow(() -> new RiderVersionNotFoundException(id));

        if (!version.getEventId().equals(eventId)) {
            throw new RiderVersionNotFoundException(id);
        }
        return version;
    }
}
