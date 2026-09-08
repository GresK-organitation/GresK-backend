package com.gresk.modules.agenda.infrastructure.outlook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.domain.port.out.CalendarEventSyncPort;
import com.gresk.modules.agenda.infrastructure.security.CalendarTokenEncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Lectura/escritura de eventos en Outlook Calendar vía Microsoft Graph REST (delta query
 * para sync incremental — equivalente al {@code syncToken} de Google, aquí se reutiliza
 * directamente la URL {@code @odata.deltaLink} como token de continuación).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutlookGraphEventSyncAdapter implements CalendarEventSyncPort {

    private static final String GRAPH_BASE = "https://graph.microsoft.com/v1.0";

    private final OutlookProperties properties;
    private final CalendarTokenEncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public CalendarProvider supportedProvider() {
        return CalendarProvider.OUTLOOK;
    }

    @Override
    public ExternalCalendarChanges pullChanges(OAuthTokenRef tokenRef, String syncToken) {
        String accessToken = decrypt(tokenRef);
        String url = syncToken != null ? syncToken
                : GRAPH_BASE + "/me/calendarView/delta?startDateTime=" + Instant.now()
                        + "&endDateTime=" + Instant.now().plusSeconds(365L * 24 * 3600);

        List<ExternalCalendarEvent> upserts = new ArrayList<>();
        List<String> deleted = new ArrayList<>();
        String newSyncToken = syncToken;

        while (url != null) {
            JsonNode page = get(url, accessToken);
            for (JsonNode item : page.withArray("value")) {
                if (item.has("@removed")) {
                    deleted.add(item.get("id").asText());
                } else {
                    upserts.add(toExternalEvent(item));
                }
            }
            url = page.hasNonNull("@odata.nextLink") ? page.get("@odata.nextLink").asText() : null;
            if (page.hasNonNull("@odata.deltaLink")) {
                newSyncToken = page.get("@odata.deltaLink").asText();
            }
        }

        return new ExternalCalendarChanges(upserts, deleted, newSyncToken);
    }

    @Override
    public String pushEvent(OAuthTokenRef tokenRef, ExternalCalendarEventDraft draft, String existingExternalEventId) {
        String accessToken = decrypt(tokenRef);
        ObjectNode body = objectMapper.createObjectNode();
        body.put("subject", draft.title());
        body.set("start", eventDateTimeNode(draft.startAt()));
        body.set("end", eventDateTimeNode(draft.endAt() != null ? draft.endAt() : draft.startAt()));

        String url = existingExternalEventId == null
                ? GRAPH_BASE + "/me/events"
                : GRAPH_BASE + "/me/events/" + existingExternalEventId;
        String method = existingExternalEventId == null ? "POST" : "PATCH";
        JsonNode response = send(method, url, accessToken, body.toString());
        return response.get("id").asText();
    }

    @Override
    public void deleteEvent(OAuthTokenRef tokenRef, String externalEventId) {
        try {
            String accessToken = decrypt(tokenRef);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GRAPH_BASE + "/me/events/" + externalEventId))
                    .header("Authorization", "Bearer " + accessToken)
                    .DELETE()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            log.warn("Could not delete Outlook event {}: {}", externalEventId, e.getMessage());
        }
    }

    @Override
    public WatchRegistration registerWatch(OAuthTokenRef tokenRef, String webhookUrl, String clientStateSecret) {
        String accessToken = decrypt(tokenRef);
        // Máximo permitido por Microsoft Graph para el recurso "event": ~4230 minutos (~2.9 días).
        Instant expiry = Instant.now().plusSeconds(4230L * 60);
        ObjectNode body = objectMapper.createObjectNode();
        body.put("changeType", "created,updated,deleted");
        body.put("notificationUrl", webhookUrl != null ? webhookUrl : properties.webhookUrl());
        body.put("resource", "me/events");
        body.put("expirationDateTime", DateTimeFormatter.ISO_INSTANT.format(expiry));
        body.put("clientState", clientStateSecret);
        body.put("id", UUID.randomUUID().toString());

        JsonNode response = send("POST", GRAPH_BASE + "/subscriptions", accessToken, body.toString());
        return new WatchRegistration(response.get("id").asText(), null, expiry);
    }

    private String decrypt(OAuthTokenRef tokenRef) {
        return encryptionService.decrypt(tokenRef.accessTokenCiphertext());
    }

    private ObjectNode eventDateTimeNode(Instant instant) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(instant.atZone(java.time.ZoneOffset.UTC)));
        node.put("timeZone", "UTC");
        return node;
    }

    private ExternalCalendarEvent toExternalEvent(JsonNode item) {
        String title = item.hasNonNull("subject") ? item.get("subject").asText() : null;
        Instant start = toInstant(item.path("start"));
        Instant end = toInstant(item.path("end"));
        String etag = item.hasNonNull("@odata.etag") ? item.get("@odata.etag").asText() : null;
        return new ExternalCalendarEvent(item.get("id").asText(), title, start, end, etag);
    }

    private Instant toInstant(JsonNode dateTimeTimeZoneNode) {
        if (dateTimeTimeZoneNode == null || dateTimeTimeZoneNode.isMissingNode() || !dateTimeTimeZoneNode.hasNonNull("dateTime")) {
            return null;
        }
        String raw = dateTimeTimeZoneNode.get("dateTime").asText();
        return java.time.LocalDateTime.parse(raw.length() > 19 ? raw.substring(0, 19) : raw)
                .atZone(java.time.ZoneOffset.UTC).toInstant();
    }

    private JsonNode get(String url, String accessToken) {
        return send("GET", url, accessToken, null);
    }

    private JsonNode send(String method, String url, String accessToken, String jsonBody) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json");
            builder = switch (method) {
                case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody));
                case "PATCH" -> builder.method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody));
                default -> builder.GET();
            };
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Microsoft Graph request failed (" + response.statusCode() + "): " + response.body());
            }
            return objectMapper.readTree(response.body());
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Microsoft Graph request failed", e);
        }
    }
}
