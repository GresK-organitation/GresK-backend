package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.port.in.CheckTerritorialExclusivityUseCase;
import com.gresk.modules.booking.application.query.CheckTerritorialExclusivityQuery;
import com.gresk.modules.booking.application.query.TerritorialConflict;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.valueobject.Territory;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.booking.domain.service.TerritorialExclusivityService;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckTerritorialExclusivityService implements CheckTerritorialExclusivityUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public List<TerritorialConflict> execute(CheckTerritorialExclusivityQuery query) {
        UUID artistId = UUID.fromString(query.artistId());
        VenueRef venue = BookingInputMapper.toVenueRef(query.venue());
        Territory territory = new Territory(query.venue().country(), query.venue().region(),
                query.venue().city(), query.venue().radiusKm());
        TerritorialExclusivity candidate = new TerritorialExclusivity(territory, query.daysBefore(), query.daysAfter());

        List<Booking> activeBookings = bookingRepository.findActiveByArtistId(artistId, PromoterId.of(query.promoterId()))
                .stream()
                .filter(b -> query.excludeBookingId() == null || !b.getId().value().toString().equals(query.excludeBookingId()))
                .toList();

        return TerritorialExclusivityService.findConflicts(venue, query.eventDate(), candidate, activeBookings).stream()
                .map(b -> new TerritorialConflict(b.getId().value().toString(), b.getVenue().venueName(), b.getEventDate()))
                .toList();
    }
}
