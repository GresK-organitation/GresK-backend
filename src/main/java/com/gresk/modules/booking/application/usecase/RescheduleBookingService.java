package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.command.RescheduleBookingCommand;
import com.gresk.modules.booking.application.port.in.RescheduleBookingUseCase;
import com.gresk.modules.booking.domain.exception.BookingNotFoundException;
import com.gresk.modules.booking.domain.exception.ForbiddenBookingOperationException;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.booking.application.event.BookingRescheduledEvent;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RescheduleBookingService implements RescheduleBookingUseCase {

    private final BookingRepositoryPort bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Booking execute(RescheduleBookingCommand command) {
        BookingId id = BookingId.of(command.bookingId());
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));
        if (!booking.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new ForbiddenBookingOperationException("Booking does not belong to promoter");
        }
        booking.reschedule(command.newEventDate());
        Booking saved = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingRescheduledEvent(this, saved.getId().value(), saved.getEventDate()));
        return saved;
    }
}
