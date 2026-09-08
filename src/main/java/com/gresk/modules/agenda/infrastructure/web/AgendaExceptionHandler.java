package com.gresk.modules.agenda.infrastructure.web;

import com.gresk.modules.agenda.domain.exception.AgendaEntryNotFoundException;
import com.gresk.modules.agenda.domain.exception.CalendarAccountAlreadyConnectedException;
import com.gresk.modules.agenda.domain.exception.CalendarAccountNotFoundException;
import com.gresk.modules.agenda.domain.exception.CalendarSyncNotConnectedException;
import com.gresk.modules.agenda.domain.exception.ForbiddenAgendaOperationException;
import com.gresk.modules.agenda.domain.exception.InvalidAgendaEntryException;
import com.gresk.modules.agenda.domain.exception.InvalidCalendarSyncStateException;
import com.gresk.modules.agenda.domain.exception.UnsupportedCalendarProviderException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AgendaExceptionHandler {

    @ExceptionHandler(AgendaEntryNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(AgendaEntryNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAgendaEntryException.class)
    ResponseEntity<Map<String, String>> handleInvalid(InvalidAgendaEntryException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenAgendaOperationException.class)
    ResponseEntity<Map<String, String>> handleForbidden(ForbiddenAgendaOperationException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CalendarAccountNotFoundException.class)
    ResponseEntity<Map<String, String>> handleCalendarAccountNotFound(CalendarAccountNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CalendarAccountAlreadyConnectedException.class)
    ResponseEntity<Map<String, String>> handleCalendarAlreadyConnected(CalendarAccountAlreadyConnectedException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({CalendarSyncNotConnectedException.class, InvalidCalendarSyncStateException.class,
            UnsupportedCalendarProviderException.class})
    ResponseEntity<Map<String, String>> handleCalendarSyncInvalidState(RuntimeException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }
}
