package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.command.CancelBookingCommand;
import com.gresk.modules.booking.application.port.in.CancelBookingUseCase;
import com.gresk.modules.booking.domain.exception.BookingNotFoundException;
import com.gresk.modules.booking.domain.exception.ForbiddenBookingOperationException;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelBookingService implements CancelBookingUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public Booking execute(CancelBookingCommand command) {
        BookingId id = BookingId.of(command.bookingId());
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));
        if (!booking.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new ForbiddenBookingOperationException("Booking does not belong to promoter");
        }
        booking.cancel(command.reason());
        return bookingRepository.save(booking);
    }
}
