package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.port.in.GetBookingUseCase;
import com.gresk.modules.booking.application.query.GetBookingQuery;
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
@Transactional(readOnly = true)
public class GetBookingService implements GetBookingUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public Booking execute(GetBookingQuery query) {
        Booking booking = bookingRepository.findById(BookingId.of(query.bookingId()))
                .orElseThrow(() -> new BookingNotFoundException(query.bookingId()));
        if (!booking.getPromoterId().equals(PromoterId.of(query.promoterId()))) {
            throw new ForbiddenBookingOperationException("Booking does not belong to promoter");
        }
        return booking;
    }
}
