package com.gresk.modules.logistics.domain.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Puerto anti-corrupción que desacopla logistics del módulo artist. Su implementación
 * en infraestructura consulta el repositorio JPA de artist directamente (infra-a-infra),
 * nunca el aggregate BandMember desde el dominio. Usado para denormalizar nombre y
 * documentación de identidad de los músicos en el travel party y el Tour Book.
 */
public interface BandMemberLogisticsQueryPort {

    Map<UUID, BandMemberLogisticsView> findViews(Set<UUID> bandMemberIds);

    record BandMemberLogisticsView(UUID bandMemberId, String name, String roleInBand, List<DocumentView> documents) {
    }

    record DocumentView(String type, String documentNumber, String issuingCountry, LocalDate expiryDate) {
    }
}
