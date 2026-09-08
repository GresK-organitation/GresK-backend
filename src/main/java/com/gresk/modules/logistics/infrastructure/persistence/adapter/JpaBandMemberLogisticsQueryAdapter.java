package com.gresk.modules.logistics.infrastructure.persistence.adapter;

import com.gresk.modules.artist.infrastructure.persistence.entity.BandMemberEntity;
import com.gresk.modules.artist.infrastructure.persistence.entity.IdentityDocumentEmbeddable;
import com.gresk.modules.artist.infrastructure.persistence.repository.BandMemberJpaRepository;
import com.gresk.modules.logistics.domain.port.out.BandMemberLogisticsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Puerto anti-corrupción hacia el módulo artist: consulta su repositorio JPA
 * directamente en infraestructura, nunca el aggregate BandMember desde el dominio.
 * Usado para las alertas de documentación caducada del Tour Book.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaBandMemberLogisticsQueryAdapter implements BandMemberLogisticsQueryPort {

    private final BandMemberJpaRepository bandMemberJpaRepository;

    @Override
    public Map<UUID, BandMemberLogisticsView> findViews(Set<UUID> bandMemberIds) {
        if (bandMemberIds == null || bandMemberIds.isEmpty()) return Map.of();
        return bandMemberJpaRepository.findAllById(bandMemberIds).stream()
                .collect(Collectors.toMap(BandMemberEntity::getId, this::toView));
    }

    private BandMemberLogisticsView toView(BandMemberEntity entity) {
        var documents = entity.getDocuments().stream().map(this::toDocumentView).toList();
        return new BandMemberLogisticsView(entity.getId(), entity.getName(), entity.getRoleInBand(), documents);
    }

    private DocumentView toDocumentView(IdentityDocumentEmbeddable d) {
        return new DocumentView(d.getType().name(), d.getDocumentNumber(), d.getIssuingCountry(), d.getExpiryDate());
    }
}
