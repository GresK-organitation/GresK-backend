package com.gresk.modules.email.infrastructure.persistence.mapper;

import com.gresk.modules.email.domain.model.PromoterGmailToken;
import com.gresk.modules.email.infrastructure.persistence.entity.PromoterGmailTokenEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

@Component
public class PromoterGmailTokenMapper {

    public PromoterGmailToken toDomain(PromoterGmailTokenEntity e) {
        return PromoterGmailToken.reconstitute(
                PromoterId.of(e.getPromoterId()),
                e.getAccessToken(),
                e.getRefreshToken(),
                e.getTokenExpiry(),
                e.getGmailAddress(),
                e.getWatchExpiry(),
                e.getConnectedAt()
        );
    }

    public PromoterGmailTokenEntity toEntity(PromoterGmailToken t) {
        return PromoterGmailTokenEntity.builder()
                .promoterId(t.getPromoterId().value())
                .accessToken(t.getAccessToken())
                .refreshToken(t.getRefreshToken())
                .tokenExpiry(t.getTokenExpiry())
                .gmailAddress(t.getGmailAddress())
                .watchExpiry(t.getWatchExpiry())
                .connectedAt(t.getConnectedAt())
                .build();
    }
}
