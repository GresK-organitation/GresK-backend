package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface EmailMessageRepositoryPort {
    EmailMessage           save(EmailMessage message);
    Optional<EmailMessage> findById(EmailMessageId id);
    Optional<EmailMessage> findByExternalMessageId(String externalMessageId);
    List<EmailMessage>     findByPromoterId(PromoterId promoterId);
    boolean                existsByExternalMessageId(String externalMessageId);
}
