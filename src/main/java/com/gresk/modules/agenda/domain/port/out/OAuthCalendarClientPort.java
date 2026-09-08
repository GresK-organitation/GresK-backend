package com.gresk.modules.agenda.domain.port.out;

import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;

/**
 * Puerto de salida para el flujo OAuth2 de un proveedor de calendario (Google/Outlook).
 * Cada implementación declara {@link #supportedProvider()}; un resolver en infraestructura
 * elige la implementación correcta en runtime (ver {@code CalendarProviderAdapterResolver}).
 */
public interface OAuthCalendarClientPort {

    CalendarProvider supportedProvider();

    String buildAuthorizationUrl(String state);

    ExternalAccountAuthResult exchangeAuthorizationCode(String authorizationCode);

    /** Refresca el access token si está caducado; devuelve el mismo ref si sigue vigente. */
    OAuthTokenRef refreshIfNeeded(OAuthTokenRef tokenRef);

    void revoke(OAuthTokenRef tokenRef);

    record ExternalAccountAuthResult(OAuthTokenRef tokenRef, String externalAccountEmail) {
    }
}
