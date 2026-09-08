package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.domain.exception.UnsupportedCalendarProviderException;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.port.out.CalendarEventSyncPort;
import com.gresk.modules.agenda.domain.port.out.OAuthCalendarClientPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Selecciona el adaptador OAuth/sync correcto para un {@link CalendarProvider} en runtime,
 * evitando ramas if/else por proveedor en los casos de uso.
 */
@Component
public class CalendarProviderAdapterResolver {

    private final List<OAuthCalendarClientPort> oAuthClients;
    private final List<CalendarEventSyncPort> eventSyncClients;

    public CalendarProviderAdapterResolver(List<OAuthCalendarClientPort> oAuthClients,
                                            List<CalendarEventSyncPort> eventSyncClients) {
        this.oAuthClients = oAuthClients;
        this.eventSyncClients = eventSyncClients;
    }

    public OAuthCalendarClientPort oAuthClientFor(CalendarProvider provider) {
        return oAuthClients.stream()
                .filter(c -> c.supportedProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new UnsupportedCalendarProviderException(provider.name()));
    }

    public CalendarEventSyncPort eventSyncClientFor(CalendarProvider provider) {
        return eventSyncClients.stream()
                .filter(c -> c.supportedProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new UnsupportedCalendarProviderException(provider.name()));
    }
}
