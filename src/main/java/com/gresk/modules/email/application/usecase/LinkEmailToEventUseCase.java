package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.exception.EventNotOwnedException;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.email.domain.port.out.EventInfoPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Vinculación manual de un email a un evento del propio promotor. */
@Service
@RequiredArgsConstructor
@Transactional
public class LinkEmailToEventUseCase {

    private final EmailMessageRepositoryPort repository;
    private final GetEmailDetailUseCase      getEmailDetail;
    private final EventInfoPort              eventInfo;

    public EmailMessage execute(UUID emailId, UUID eventId, UUID promoterId) {
        EmailMessage email = getEmailDetail.execute(emailId, promoterId);

        eventInfo.findEventTitle(eventId, PromoterId.of(promoterId))
                .orElseThrow(() -> new EventNotOwnedException(eventId));

        email.linkToEvent(eventId);
        return repository.save(email);
    }
}
