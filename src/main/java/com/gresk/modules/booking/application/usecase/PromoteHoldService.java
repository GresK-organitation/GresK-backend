package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.command.PromoteHoldCommand;
import com.gresk.modules.booking.application.port.in.PromoteHoldUseCase;
import com.gresk.modules.booking.domain.exception.BookingNotFoundException;
import com.gresk.modules.booking.domain.exception.ForbiddenBookingOperationException;
import com.gresk.modules.booking.domain.exception.InvalidBookingException;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.model.BookingStatus;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.booking.application.event.HoldConfirmedEvent;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoteHoldService implements PromoteHoldUseCase {

    private final BookingRepositoryPort bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Booking execute(PromoteHoldCommand command) {
        BookingId id = BookingId.of(command.bookingId());
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));
        if (!booking.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new ForbiddenBookingOperationException("Booking does not belong to promoter");
        }

        BookingStatus target = BookingStatus.valueOf(command.targetStatus());
        switch (target) {
            case HOLD_2 -> booking.promoteToHold2(command.newHoldExpiresAt());
            case CONFIRMED -> booking.confirm();
            default -> throw new InvalidBookingException("Unsupported promotion target: " + target);
        }

        Booking saved = bookingRepository.save(booking);
        if (saved.getStatus() == BookingStatus.CONFIRMED) {
            eventPublisher.publishEvent(new HoldConfirmedEvent(this, saved.getId().value(), saved.getArtistId()));
        }
        return saved;
    }
}
