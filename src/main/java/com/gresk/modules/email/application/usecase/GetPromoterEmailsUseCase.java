package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Bandeja completa del promotor (orden cronológico inverso). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPromoterEmailsUseCase {

    private final EmailMessageRepositoryPort repository;

    public List<EmailMessage> execute(UUID promoterId) {
        return repository.findByPromoterId(PromoterId.of(promoterId));
    }
}
