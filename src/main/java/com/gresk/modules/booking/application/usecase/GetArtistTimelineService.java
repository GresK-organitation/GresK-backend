package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.port.in.GetArtistTimelineUseCase;
import com.gresk.modules.booking.application.query.ArtistTimelineQuery;
import com.gresk.modules.booking.application.query.TimelineEntry;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.MilestoneStatus;
import com.gresk.modules.booking.domain.model.valueobject.Milestone;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtistTimelineService implements GetArtistTimelineUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public List<TimelineEntry> execute(ArtistTimelineQuery query) {
        UUID artistId = UUID.fromString(query.artistId());
        return bookingRepository.findByPromoterAndDateRange(PromoterId.of(query.promoterId()), query.from(), query.to())
                .stream()
                .filter(b -> b.getArtistId().equals(artistId))
                .sorted(Comparator.comparing(Booking::getEventDate))
                .map(this::toTimelineEntry)
                .toList();
    }

    private TimelineEntry toTimelineEntry(Booking booking) {
        List<Milestone> pending = booking.getMilestones().stream()
                .filter(m -> m.status() == MilestoneStatus.PENDING)
                .sorted(Comparator.comparing(Milestone::dueDate))
                .toList();
        String nextTitle = pending.isEmpty() ? null : pending.get(0).title();
        return new TimelineEntry(booking.getId().toString(), booking.getVenue().venueName(), booking.getEventDate(),
                booking.getStatus().name(), pending.size(), nextTitle);
    }
}
