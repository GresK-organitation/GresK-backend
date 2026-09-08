package com.gresk.modules.show.infrastructure.event;

import com.gresk.modules.event.application.usecase.CreateEventCommand;
import com.gresk.modules.event.application.usecase.CreateEventUseCase;
import com.gresk.modules.event.application.usecase.PublishEventUseCase;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.show.domain.port.out.MarketplaceListingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptador anti-corrupción hacia el módulo {@code event} (ver Javadoc de
 * {@link MarketplaceListingPort}). Crea el listado en {@code DRAFT} y lo publica en el mismo
 * paso porque, en este flujo, "abrir venta" ya implica que el show está {@code CONFIRMADO}
 * y listo para salir al público -- no existe un estado intermedio de listado sin publicar.
 * La ubicación del recinto no se traslada automáticamente: el equipo de marketing la completa
 * (o la ficha se enriquece más adelante) directamente sobre el listado ya creado.
 */
@Component
@RequiredArgsConstructor
public class MarketplaceListingAdapter implements MarketplaceListingPort {

    private final CreateEventUseCase  createEventUseCase;
    private final PublishEventUseCase publishEventUseCase;

    @Override
    public UUID publishListing(PublishListingRequest request) {
        CreateEventCommand command = new CreateEventCommand(
                request.promoterId(),
                request.title(),
                request.genre(),
                request.ticketPrice(),
                request.currency(),
                request.totalCapacity(),
                request.eventDate(),
                null,   // revealAt: sin countdown de reveal para shows importados desde production
                null, null, null, null, null, null,  // ubicación: se completa después sobre el listado
                null,   // coverImageFile
                null,   // artistId
                false, null, null // flash deal: se configura después, no al abrir venta
        );

        Event created = createEventUseCase.execute(command);
        Event published = publishEventUseCase.execute(created.getId().toString(), request.promoterId());
        return published.getId().value();
    }
}
