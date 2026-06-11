package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.PromoterGmailToken;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.Optional;

public interface PromoterGmailTokenRepositoryPort {
    PromoterGmailToken           save(PromoterGmailToken token);
    Optional<PromoterGmailToken> findByPromoterId(PromoterId promoterId);
    Optional<PromoterGmailToken> findByGmailAddress(String gmailAddress);
    void                         deleteByPromoterId(PromoterId promoterId);
}
