package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.exception.PromoterNotActiveException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.port.out.AccountStatusPort;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Coordinates;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateEventUseCase {

    private final EventRepository eventRepository;
    private final ImageStoragePort imageStorage;
    private final AccountStatusPort accountStatusPort;

    @Transactional
    public Event execute(CreateEventCommand command) {
        UUID accountId = UUID.fromString(command.promoterId());
        if (accountStatusPort.getStatus(accountId) != AccountStatus.ACTIVE) {
            throw new PromoterNotActiveException("Promoter account is not active: " + accountId);
        }

        PromoterId promoterId = PromoterId.of(command.promoterId());
        Event event = Event.create(command.title(), promoterId);

        String coverImageAssetId = null;
        if (command.coverImageFile() != null && !command.coverImageFile().isEmpty()) {
            try {
                coverImageAssetId = imageStorage.upload(command.coverImageFile(), "events/covers").value();
            } catch (Exception e) {
                log.warn("Could not upload cover image: {}", e.getMessage());
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
        if (command.artistId() != null && !command.artistId().isBlank()) {
            event.withArtistId(UUID.fromString(command.artistId()));
        }

        return eventRepository.save(event);
    }
}
