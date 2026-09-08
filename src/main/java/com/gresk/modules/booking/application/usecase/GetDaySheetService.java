package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.port.in.GetDaySheetUseCase;
import com.gresk.modules.booking.application.query.GetDaySheetQuery;
import com.gresk.modules.booking.domain.exception.BookingNotFoundException;
import com.gresk.modules.booking.domain.exception.ForbiddenBookingOperationException;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.model.valueobject.DaySheet;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDaySheetService implements GetDaySheetUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public DaySheet execute(GetDaySheetQuery query) {
        Booking booking = bookingRepository.findById(BookingId.of(query.bookingId()))
                .orElseThrow(() -> new BookingNotFoundException(query.bookingId()));
        if (!booking.getPromoterId().equals(PromoterId.of(query.promoterId()))) {
            throw new ForbiddenBookingOperationException("Booking does not belong to promoter");
        }
        return booking.getDaySheet();
    }
}
