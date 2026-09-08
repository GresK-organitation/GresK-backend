package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.port.in.ExpireHoldsUseCase;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpireHoldsService implements ExpireHoldsUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public int execute() {
        List<Booking> expirable = bookingRepository.findExpirable(Instant.now());
        for (Booking booking : expirable) {
            booking.expire();
            bookingRepository.save(booking);
        }
        return expirable.size();
    }
}
