package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.command.CreateHoldCommand;
import com.gresk.modules.booking.application.port.in.CreateHoldUseCase;
import com.gresk.modules.booking.domain.exception.TerritorialExclusivityConflictException;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingStatus;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.booking.domain.service.TerritorialExclusivityService;
import com.gresk.modules.booking.application.event.HoldCreatedEvent;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateHoldService implements CreateHoldUseCase {

    private final BookingRepositoryPort bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Booking execute(CreateHoldCommand command) {
        PromoterId promoterId = PromoterId.of(command.promoterId());
        UUID artistId = UUID.fromString(command.artistId());
        VenueRef venue = BookingInputMapper.toVenueRef(command.venue());
        TerritorialExclusivity exclusivity = BookingInputMapper.toExclusivity(command.exclusivity());
        BookingStatus initialStatus = BookingStatus.valueOf(command.holdLevel());

        if (exclusivity != null) {
            List<Booking> activeBookings = bookingRepository.findActiveByArtistId(artistId, promoterId);
            List<Booking> conflicts = TerritorialExclusivityService.findConflicts(
                    venue, command.eventDate(), exclusivity, activeBookings);
            if (!conflicts.isEmpty() && !command.forceIgnoreConflicts()) {
                throw new TerritorialExclusivityConflictException(conflicts.stream().map(b -> b.getId().value()).toList());
            }
        }

        Booking booking = Booking.create(promoterId, artistId, venue, command.eventDate(), initialStatus,
                command.holdExpiresAt(), BookingInputMapper.toBlueprints(command.milestoneBlueprints()),
                exclusivity, command.notes());

        Booking saved = bookingRepository.save(booking);
        eventPublisher.publishEvent(new HoldCreatedEvent(this, saved.getId().value(), saved.getArtistId()));
        return saved;
    }
}
