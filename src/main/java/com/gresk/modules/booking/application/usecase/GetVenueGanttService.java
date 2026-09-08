package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.port.in.GetVenueGanttUseCase;
import com.gresk.modules.booking.application.query.GanttBar;
import com.gresk.modules.booking.application.query.VenueGanttQuery;
import com.gresk.modules.booking.application.query.VenueGanttRow;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.port.out.BookingArtistQueryPort;
import com.gresk.modules.booking.domain.port.out.BookingArtistQueryPort.BookingArtistView;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Agrupa bookings por venue en un rango de fechas, estilo Gantt. Agrupa por
 * {@code venue.venueId} cuando está presente, si no por la clave compuesta
 * (venueName, city) — limitación conocida mientras no exista un aggregate Venue real.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetVenueGanttService implements GetVenueGanttUseCase {

    private static final Duration DEFAULT_BAR_DURATION = Duration.ofHours(4);

    private final BookingRepositoryPort bookingRepository;
    private final BookingArtistQueryPort artistQueryPort;

    @Override
    public List<VenueGanttRow> execute(VenueGanttQuery query) {
        PromoterId promoterId = PromoterId.of(query.promoterId());
        UUID venueFilter = query.venueId() == null || query.venueId().isBlank() ? null : UUID.fromString(query.venueId());

        List<Booking> bookings = bookingRepository.findByPromoterAndDateRange(promoterId, query.from(), query.to())
                .stream()
                .filter(b -> venueFilter == null || venueFilter.equals(b.getVenue().venueId()))
                .toList();

        Set<UUID> artistIds = bookings.stream().map(Booking::getArtistId).collect(Collectors.toSet());
        Map<UUID, BookingArtistView> artistViews = artistQueryPort.findArtistViews(artistIds);

        Map<String, VenueGroup> groups = new LinkedHashMap<>();
        for (Booking booking : bookings) {
            String key = booking.getVenue().venueId() != null
                    ? booking.getVenue().venueId().toString()
                    : booking.getVenue().venueName() + "|" + booking.getVenue().territory().city();
            VenueGroup group = groups.computeIfAbsent(key,
                    k -> new VenueGroup(booking.getVenue().venueId() == null ? null : booking.getVenue().venueId().toString(),
                            booking.getVenue().venueName()));
            String artistName = artistViews.containsKey(booking.getArtistId())
                    ? artistViews.get(booking.getArtistId()).name()
                    : booking.getArtistId().toString();
            group.bars.add(new GanttBar(booking.getId().toString(), artistName, booking.getEventDate(),
                    booking.getEventDate().plus(DEFAULT_BAR_DURATION), booking.getStatus().name()));
        }

        return groups.values().stream()
                .map(g -> new VenueGanttRow(g.venueId, g.venueName, g.bars))
                .toList();
    }

    private static final class VenueGroup {
        final String venueId;
        final String venueName;
        final List<GanttBar> bars = new java.util.ArrayList<>();

        VenueGroup(String venueId, String venueName) {
            this.venueId = venueId;
            this.venueName = venueName;
        }
    }
}
