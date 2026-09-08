package com.gresk.modules.logistics.infrastructure.web;

import com.gresk.modules.logistics.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class LogisticsExceptionHandler {

    @ExceptionHandler({TourNotFoundException.class, TravelPartyNotFoundException.class,
            CrewMemberNotFoundException.class, ItineraryNotFoundException.class, RoomingListNotFoundException.class})
    ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenLogisticsOperationException.class)
    ResponseEntity<Map<String, String>> handleForbidden(ForbiddenLogisticsOperationException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({InvalidTourException.class, InvalidTravelPartyException.class, InvalidCrewMemberException.class,
            InvalidItineraryException.class, InvalidRoomingListException.class, InsufficientRoomAllotmentException.class})
    ResponseEntity<Map<String, String>> handleInvalid(RuntimeException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }
}
