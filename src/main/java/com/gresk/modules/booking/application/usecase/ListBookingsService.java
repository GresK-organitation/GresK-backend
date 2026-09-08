package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.port.in.ListBookingsUseCase;
import com.gresk.modules.booking.application.query.ListBookingsQuery;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingStatus;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListBookingsService implements ListBookingsUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public List<Booking> execute(ListBookingsQuery query) {
        BookingStatus status = query.status() == null || query.status().isBlank()
                ? null : BookingStatus.valueOf(query.status());
        return bookingRepository.findByPromoter(PromoterId.of(query.promoterId()), query.from(), query.to(), status);
    }
}
