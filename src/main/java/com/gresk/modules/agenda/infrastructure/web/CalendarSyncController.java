package com.gresk.modules.agenda.infrastructure.web;

import com.gresk.modules.agenda.application.command.CompleteCalendarConnectionCommand;
import com.gresk.modules.agenda.application.command.DisconnectCalendarCommand;
import com.gresk.modules.agenda.application.command.SyncCalendarCommand;
import com.gresk.modules.agenda.application.port.in.CompleteCalendarConnectionUseCase;
import com.gresk.modules.agenda.application.port.in.DisconnectCalendarUseCase;
import com.gresk.modules.agenda.application.port.in.GetCalendarAuthorizationUrlUseCase;
import com.gresk.modules.agenda.application.port.in.ListCalendarAccountsUseCase;
import com.gresk.modules.agenda.application.port.in.SyncCalendarUseCase;
import com.gresk.modules.agenda.application.query.GetCalendarAuthorizationUrlQuery;
import com.gresk.modules.agenda.application.query.ListCalendarAccountsQuery;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.infrastructure.web.dto.AuthorizationUrlResponse;
import com.gresk.modules.agenda.infrastructure.web.dto.CalendarSyncAccountResponse;
import com.gresk.modules.agenda.infrastructure.web.dto.SyncResultResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agenda/calendar-sync")
@RequiredArgsConstructor
@Tag(name = "CalendarSync", description = "Sincronización bidireccional con Google Calendar y Outlook")
public class CalendarSyncController {

    private final GetCalendarAuthorizationUrlUseCase getCalendarAuthorizationUrlUseCase;
    private final CompleteCalendarConnectionUseCase completeCalendarConnectionUseCase;
    private final SyncCalendarUseCase syncCalendarUseCase;
    private final DisconnectCalendarUseCase disconnectCalendarUseCase;
    private final ListCalendarAccountsUseCase listCalendarAccountsUseCase;

    @PreAuthorize("hasRole('PROMOTER')")
    @GetMapping("/{provider}/authorize")
    public AuthorizationUrlResponse authorize(@AuthenticationPrincipal String promoterId, @PathVariable String provider) {
        String url = getCalendarAuthorizationUrlUseCase.execute(
                new GetCalendarAuthorizationUrlQuery(promoterId, provider.toUpperCase()));
        return new AuthorizationUrlResponse(url);
    }

    /** Callback público del proveedor OAuth — sin JWT, protegido por el state anti-CSRF. */
    @GetMapping("/{provider}/callback")
    public CalendarSyncAccountResponse callback(@PathVariable String provider,
                                                 @RequestParam String code, @RequestParam String state) {
        var command = new CompleteCalendarConnectionCommand(state, code, provider.toUpperCase());
        return toResponse(completeCalendarConnectionUseCase.execute(command));
    }

    @PreAuthorize("hasRole('PROMOTER')")
    @GetMapping
    public List<CalendarSyncAccountResponse> listAccounts(@AuthenticationPrincipal String promoterId) {
        return listCalendarAccountsUseCase.execute(new ListCalendarAccountsQuery(promoterId)).stream()
                .map(this::toResponse).toList();
    }

    @PreAuthorize("hasRole('PROMOTER')")
    @PostMapping("/{id}/sync-now")
    public SyncResultResponse syncNow(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        var result = syncCalendarUseCase.execute(new SyncCalendarCommand(id, promoterId));
        return new SyncResultResponse(result.pushedCount(), result.pulledCount(), result.conflictCount(), result.syncedAt());
    }

    @PreAuthorize("hasRole('PROMOTER')")
    @DeleteMapping("/{id}")
    public void disconnect(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        disconnectCalendarUseCase.execute(new DisconnectCalendarCommand(id, promoterId));
    }

    private CalendarSyncAccountResponse toResponse(CalendarSyncAccount account) {
        return new CalendarSyncAccountResponse(account.getId().toString(), account.getProvider().name(),
                account.getExternalAccountEmail(), account.getStatus().name(), account.getLastSyncedAt(),
                account.getLastError(), account.getCreatedAt());
    }
}
