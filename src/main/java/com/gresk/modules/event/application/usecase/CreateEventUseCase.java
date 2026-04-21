package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Coordinates;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateEventUseCase {

    private final EventRepository eventRepository;
    private final ImageStoragePort imageStorage;

    @Transactional
    public Event execute(CreateEventCommand command) {
        PromoterId promoterId = PromoterId.of(command.promoterId());
        Event event = Event.create(command.title(), promoterId);
        String coverImageAssetId = null;
        if (command.coverImageAssetId() != null && !command.coverImageAssetId().isEmpty()) {
            try {
                coverImageAssetId = imageStorage.upload(command.coverImageAssetId(), "events/covers").value();
            } catch (Exception e) {
                log.warn("Could not upload cover image: {}", e.getMessage());
            }
        }

        String artistImageAssetId = null;
        if (command.artistImageUrl() != null && !command.artistImageUrl().isEmpty()) {
            try {
                artistImageAssetId = imageStorage.upload(command.artistImageUrl(), "events/artists").value();
            } catch (Exception e) {
                log.warn("Could not upload artist image: {}", e.getMessage());
            }
        }

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
        if (coverImageAssetId != null && !coverImageAssetId.isBlank()) {
            event.withCoverImage(AssetId.of(coverImageAssetId));
        }
        if (command.artistName() != null && !command.artistName().isBlank()) {
            AssetId artistAssetId = (artistImageAssetId != null && !artistImageAssetId.isBlank())
                    ? AssetId.of(artistImageAssetId) : null;
            event.withArtist(Artist.of(command.artistName(), artistAssetId));
        }

        return eventRepository.save(event);
    }
}
