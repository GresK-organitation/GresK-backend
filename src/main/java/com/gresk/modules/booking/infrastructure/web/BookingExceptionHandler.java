package com.gresk.modules.booking.infrastructure.web;

import com.gresk.modules.booking.domain.exception.BookingNotFoundException;
import com.gresk.modules.booking.domain.exception.ForbiddenBookingOperationException;
import com.gresk.modules.booking.domain.exception.HoldExpirationRequiredException;
import com.gresk.modules.booking.domain.exception.InvalidBookingException;
import com.gresk.modules.booking.domain.exception.InvalidBookingStatusTransitionException;
import com.gresk.modules.booking.domain.exception.MilestoneAlreadyCompletedException;
import com.gresk.modules.booking.domain.exception.MilestoneNotFoundException;
import com.gresk.modules.booking.domain.exception.TerritorialExclusivityConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.gresk.modules.booking")
public class BookingExceptionHandler {

    @ExceptionHandler({BookingNotFoundException.class, MilestoneNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenBookingOperationException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenBookingOperationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({InvalidBookingException.class, InvalidBookingStatusTransitionException.class,
            HoldExpirationRequiredException.class, MilestoneAlreadyCompletedException.class})
    public ResponseEntity<Map<String, String>> handleUnprocessable(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TerritorialExclusivityConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(TerritorialExclusivityConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", ex.getMessage(),
                "conflictingBookingIds", ex.getConflictingBookingIds()));
    }
}
