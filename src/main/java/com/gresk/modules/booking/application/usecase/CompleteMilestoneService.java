package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.command.CompleteMilestoneCommand;
import com.gresk.modules.booking.application.port.in.CompleteMilestoneUseCase;
import com.gresk.modules.booking.domain.exception.BookingNotFoundException;
import com.gresk.modules.booking.domain.exception.ForbiddenBookingOperationException;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompleteMilestoneService implements CompleteMilestoneUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public Booking execute(CompleteMilestoneCommand command) {
        BookingId id = BookingId.of(command.bookingId());
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));
        if (!booking.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new ForbiddenBookingOperationException("Booking does not belong to promoter");
        }
        booking.completeMilestone(UUID.fromString(command.milestoneId()), command.notes());
        return bookingRepository.save(booking);
    }
}
