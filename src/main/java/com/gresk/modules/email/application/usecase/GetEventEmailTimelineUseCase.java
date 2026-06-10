package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Timeline de correos de un evento (orden cronológico inverso). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEventEmailTimelineUseCase {

    private final EmailMessageRepositoryPort repository;

    public List<EmailMessage> execute(UUID eventId, UUID promoterId) {
        return repository.findByEventIdAndPromoterId(eventId, PromoterId.of(promoterId));
    }
}
