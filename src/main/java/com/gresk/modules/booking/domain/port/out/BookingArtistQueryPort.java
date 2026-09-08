package com.gresk.modules.booking.domain.port.out;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Puerto anti-corrupción que desacopla {@code booking} del módulo {@code artist}.
 * Su implementación en infraestructura consulta el repositorio JPA del módulo artist
 * directamente (infra-a-infra), nunca el aggregate {@code Artist} desde el dominio.
 */
public interface BookingArtistQueryPort {

    /** Resolución en bloque (evita N+1 al construir las vistas Gantt/Timeline). */
    Map<UUID, BookingArtistView> findArtistViews(Set<UUID> artistIds);

    record BookingArtistView(UUID artistId, String name, String imageUrl) {
    }
}
