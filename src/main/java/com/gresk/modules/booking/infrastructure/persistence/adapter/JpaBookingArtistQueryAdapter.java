package com.gresk.modules.booking.infrastructure.persistence.adapter;

import com.gresk.modules.artist.infrastructure.persistence.ArtistEntity;
import com.gresk.modules.artist.infrastructure.persistence.ArtistJpaRepository;
import com.gresk.modules.booking.domain.port.out.BookingArtistQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Puerto anti-corrupción hacia el módulo {@code artist}: consulta su repositorio JPA
 * directamente en infraestructura, nunca el aggregate {@code Artist} desde el dominio.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaBookingArtistQueryAdapter implements BookingArtistQueryPort {

    private final ArtistJpaRepository artistJpaRepository;

    @Override
    public Map<UUID, BookingArtistView> findArtistViews(Set<UUID> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) return Map.of();
        return artistJpaRepository.findAllById(artistIds).stream()
                .collect(Collectors.toMap(ArtistEntity::getId,
                        a -> new BookingArtistView(a.getId(), a.getName(), a.getImageAssetId())));
    }
}
