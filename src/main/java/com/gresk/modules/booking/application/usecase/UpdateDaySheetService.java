package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.command.UpdateDaySheetCommand;
import com.gresk.modules.booking.application.port.in.UpdateDaySheetUseCase;
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

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateDaySheetService implements UpdateDaySheetUseCase {

    private final BookingRepositoryPort bookingRepository;

    @Override
    public Booking execute(UpdateDaySheetCommand command) {
        Booking booking = bookingRepository.findById(BookingId.of(command.bookingId()))
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));
        if (!booking.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new ForbiddenBookingOperationException("Booking does not belong to promoter");
        }
        DaySheet daySheet = new DaySheet(LocalDate.parse(command.showDate()),
                BookingInputMapper.toDaySheetEntries(command.entries()));
        booking.assignDaySheet(daySheet);
        return bookingRepository.save(booking);
    }
}
