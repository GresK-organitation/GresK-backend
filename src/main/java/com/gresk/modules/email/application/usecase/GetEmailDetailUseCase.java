package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.exception.EmailMessageNotFoundException;
import com.gresk.modules.email.domain.exception.ForbiddenEmailOperationException;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEmailDetailUseCase {

    private final EmailMessageRepositoryPort repository;

    public EmailMessage execute(UUID emailId, UUID promoterId) {
        EmailMessageId id = EmailMessageId.of(emailId);
        EmailMessage email = repository.findById(id)
                .orElseThrow(() -> new EmailMessageNotFoundException(id));

        if (!email.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ForbiddenEmailOperationException(
                    "Email " + emailId + " does not belong to promoter " + promoterId);
        }
        return email;
    }
}
