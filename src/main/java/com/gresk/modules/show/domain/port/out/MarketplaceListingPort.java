package com.gresk.modules.show.domain.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Puerto anti-corrupción hacia el módulo {@code event} (el listado público de venta de
 * entradas). Se invoca únicamente en la transición {@code CONFIRMADO -> EN_VENTA}: el
 * adaptador de infraestructura traduce esta llamada a {@code event.CreateEventUseCase} +
 * {@code event.PublishEventUseCase}, de forma que {@code show} nunca conoce el modelo interno
 * del módulo {@code event} (ni al revés).
 */
public interface MarketplaceListingPort {
    UUID publishListing(PublishListingRequest request);

    record PublishListingRequest(
            UUID showId,
            String promoterId,
            String title,
            String genre,
            Instant eventDate,
            int totalCapacity,
            BigDecimal ticketPrice,
            String currency
    ) {
    }
}
