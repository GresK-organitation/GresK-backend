package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.exception.EventNotOwnedException;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.email.domain.port.out.EventInfoPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Timeline paginado de correos de un evento (orden cronológico inverso). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEventEmailTimelineUseCase {

    private final EmailMessageRepositoryPort repository;
    private final EventInfoPort              eventInfo;

    public List<EmailMessage> execute(UUID eventId, UUID promoterId, int page, int size) {
        PromoterId promoter = PromoterId.of(promoterId);
        eventInfo.findEventTitle(eventId, promoter)
                .orElseThrow(() -> new EventNotOwnedException(eventId));

        return repository.findByEventIdAndPromoterId(eventId, promoter, page, size);
    }
}
