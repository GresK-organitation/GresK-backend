package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.model.PromoterGmailToken;
import com.gresk.modules.email.domain.port.out.PromoterGmailTokenRepositoryPort;
import com.gresk.modules.email.infrastructure.persistence.entity.PromoterGmailTokenEntity;
import com.gresk.modules.email.infrastructure.persistence.mapper.PromoterGmailTokenMapper;
import com.gresk.modules.email.infrastructure.persistence.repository.PromoterGmailTokenJpaRepository;
import com.gresk.modules.email.infrastructure.security.TokenEncryptionService;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Los tokens OAuth se cifran (AES-256-GCM) justo antes de persistir y se
 * descifran al cargar: el dominio y los casos de uso trabajan en claro.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPromoterGmailTokenRepositoryAdapter implements PromoterGmailTokenRepositoryPort {

    private final PromoterGmailTokenJpaRepository repo;
    private final PromoterGmailTokenMapper        mapper;
    private final TokenEncryptionService          encryption;

    @Override
    @Transactional
    public PromoterGmailToken save(PromoterGmailToken token) {
        PromoterGmailTokenEntity entity = mapper.toEntity(token);
        entity.setAccessToken(encryption.encrypt(token.getAccessToken()));
        entity.setRefreshToken(encryption.encrypt(token.getRefreshToken()));
        repo.save(entity);
        return token;
    }

    @Override
    public Optional<PromoterGmailToken> findByPromoterId(PromoterId promoterId) {
        return repo.findById(promoterId.value()).map(this::toDecryptedDomain);
    }

    @Override
    public Optional<PromoterGmailToken> findByGmailAddress(String gmailAddress) {
        return repo.findByGmailAddress(gmailAddress).map(this::toDecryptedDomain);
    }

    @Override
    @Transactional
    public void deleteByPromoterId(PromoterId promoterId) {
        repo.deleteById(promoterId.value());
    }

    private PromoterGmailToken toDecryptedDomain(PromoterGmailTokenEntity entity) {
        entity.setAccessToken(encryption.decrypt(entity.getAccessToken()));
        entity.setRefreshToken(encryption.decrypt(entity.getRefreshToken()));
        return mapper.toDomain(entity);
    }
}
