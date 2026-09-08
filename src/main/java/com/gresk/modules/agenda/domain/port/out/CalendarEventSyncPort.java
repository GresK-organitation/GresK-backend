package com.gresk.modules.agenda.domain.port.out;

import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida para leer/escribir eventos en el calendario externo de un proveedor.
 * Cada implementación declara {@link #supportedProvider()} (ver {@code OAuthCalendarClientPort}).
 */
public interface CalendarEventSyncPort {

    CalendarProvider supportedProvider();

    /** Sync incremental si {@code syncToken} no es null; sync completo si lo es. */
    ExternalCalendarChanges pullChanges(OAuthTokenRef tokenRef, String syncToken);

    /** @return el id externo del evento creado/actualizado */
    String pushEvent(OAuthTokenRef tokenRef, ExternalCalendarEventDraft draft, String existingExternalEventId);

    void deleteEvent(OAuthTokenRef tokenRef, String externalEventId);

    /** Registra un canal/suscripción de notificaciones push; expiración según el proveedor. */
    WatchRegistration registerWatch(OAuthTokenRef tokenRef, String webhookUrl, String clientStateSecret);

    record ExternalCalendarChanges(List<ExternalCalendarEvent> upserts, List<String> deletedExternalIds,
                                    String newSyncToken) {
    }

    record ExternalCalendarEvent(String externalEventId, String title, Instant startAt, Instant endAt, String etag) {
    }

    record ExternalCalendarEventDraft(String title, Instant startAt, Instant endAt) {
    }

    record WatchRegistration(String channelOrSubscriptionId, String resourceId, Instant expiry) {
    }
}
