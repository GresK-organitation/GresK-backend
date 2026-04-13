package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Coordinates;
import com.gresk.shared.domain.valueobject.ImageUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateEventUseCase {

    private final EventRepository eventRepository;

    @Transactional
    public Event execute(CreateEventCommand command) {
        PromoterId promoterId = PromoterId.of(command.promoterId());
        Event event = Event.create(command.title(), promoterId);

        if (command.genre() != null) {
            event.withGenre(MusicGenre.valueOf(command.genre()));
        }
        if (command.amount() != null) {
            event.withPrice(new Price(command.amount(), command.currency()));
        }
        if (command.totalCapacity() != null) {
            event.withCapacity(Capacity.of(command.totalCapacity()));
        }
        if (command.eventDate() != null) {
            event.withEventDate(command.eventDate());
        }
        if (command.street() != null && command.city() != null && command.country() != null
                && command.latitude() != null && command.longitude() != null) {
            Address address = new Address(command.street(), City.of(command.city()), command.country());
            Coordinates coords = Coordinates.of(command.latitude(), command.longitude());
            event.withLocation(new Location(address, coords, command.venue()));
        }
        if (command.revealAt() != null) {
            event.withRevealAt(command.revealAt());
        }
        if (command.coverImageUrl() != null && !command.coverImageUrl().isBlank()) {
            event.withCoverImage(ImageUrl.of(command.coverImageUrl()));
        }
        if (command.artistName() != null && !command.artistName().isBlank()) {
            ImageUrl artistImg = (command.artistImageUrl() != null && !command.artistImageUrl().isBlank())
                    ? ImageUrl.of(command.artistImageUrl()) : null;
            event.withArtist(Artist.of(command.artistName(), artistImg));
        }

        return eventRepository.save(event);
    }
}
