package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.port.out.EventInfoPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Consulta de solo lectura al módulo de eventos (mismo patrón que
 * JpaPromoterEventQueryAdapter en agenda): título del evento si pertenece
 * al promotor, sin acoplar el módulo email a las clases de event.
 */
@Component
@RequiredArgsConstructor
public class JpaEventInfoAdapter implements EventInfoPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<String> findEventTitle(UUID eventId, PromoterId promoterId) {
        return jdbcTemplate.query(
                "SELECT title FROM events WHERE id = ? AND promoter_id = ?",
                (rs, rowNum) -> rs.getString("title"),
                eventId, promoterId.value()
        ).stream().findFirst();
    }
}
