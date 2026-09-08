package com.gresk.modules.agenda.infrastructure.google;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.domain.port.out.CalendarEventSyncPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Lectura/escritura de eventos en Google Calendar (calendario "primary" del usuario conectado). */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleCalendarEventSyncAdapter implements CalendarEventSyncPort {

    private static final String PRIMARY_CALENDAR = "primary";

    private final GoogleCredentialsFactory credentialsFactory;
    private final GoogleCalendarProperties properties;

    @Override
    public CalendarProvider supportedProvider() {
        return CalendarProvider.GOOGLE;
    }

    @Override
    public ExternalCalendarChanges pullChanges(OAuthTokenRef tokenRef, String syncToken) {
        Calendar calendar = credentialsFactory.clientFor(tokenRef);
        try {
            return listEvents(calendar, syncToken);
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 410) {
                log.warn("Google Calendar syncToken expired — falling back to full sync");
                try {
                    return listEvents(calendar, null);
                } catch (GoogleJsonResponseException e2) {
                    throw new IllegalStateException("Cannot pull Google Calendar changes", e2);
                }
            }
            throw new IllegalStateException("Cannot pull Google Calendar changes", e);
        }
    }

    private ExternalCalendarChanges listEvents(Calendar calendar, String syncToken) throws GoogleJsonResponseException {
        try {
            List<ExternalCalendarEvent> upserts = new ArrayList<>();
            List<String> deleted = new ArrayList<>();
            String pageToken = null;
            String newSyncToken = syncToken;

            do {
                Calendar.Events.List request = calendar.events().list(PRIMARY_CALENDAR)
                        .setPageToken(pageToken)
                        .setShowDeleted(true)
                        .setSingleEvents(true);
                if (syncToken != null) {
                    request.setSyncToken(syncToken);
                } else {
                    request.setTimeMin(new DateTime(Instant.now().toEpochMilli()));
                }

                Events response = request.execute();
                for (Event event : Optional.ofNullable(response.getItems()).orElse(List.of())) {
                    if ("cancelled".equals(event.getStatus())) {
                        deleted.add(event.getId());
                    } else {
                        upserts.add(toExternalEvent(event));
                    }
                }
                pageToken = response.getNextPageToken();
                if (response.getNextSyncToken() != null) {
                    newSyncToken = response.getNextSyncToken();
                }
            } while (pageToken != null);

            return new ExternalCalendarChanges(upserts, deleted, newSyncToken);
        } catch (GoogleJsonResponseException e) {
            throw e;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot list Google Calendar events", e);
        }
    }

    @Override
    public String pushEvent(OAuthTokenRef tokenRef, ExternalCalendarEventDraft draft, String existingExternalEventId) {
        Calendar calendar = credentialsFactory.clientFor(tokenRef);
        try {
            Event event = new Event()
                    .setSummary(draft.title())
                    .setStart(toEventDateTime(draft.startAt()))
                    .setEnd(toEventDateTime(draft.endAt() != null ? draft.endAt() : draft.startAt()));

            Event result = existingExternalEventId == null
                    ? calendar.events().insert(PRIMARY_CALENDAR, event).execute()
                    : calendar.events().update(PRIMARY_CALENDAR, existingExternalEventId, event).execute();
            return result.getId();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot push event to Google Calendar", e);
        }
    }

    @Override
    public void deleteEvent(OAuthTokenRef tokenRef, String externalEventId) {
        Calendar calendar = credentialsFactory.clientFor(tokenRef);
        try {
            calendar.events().delete(PRIMARY_CALENDAR, externalEventId).execute();
        } catch (Exception e) {
            log.warn("Could not delete Google Calendar event {}: {}", externalEventId, e.getMessage());
        }
    }

    @Override
    public WatchRegistration registerWatch(OAuthTokenRef tokenRef, String webhookUrl, String clientStateSecret) {
        Calendar calendar = credentialsFactory.clientFor(tokenRef);
        try {
            Channel channel = new Channel()
                    .setId(UUID.randomUUID().toString())
                    .setType("web_hook")
                    .setAddress(webhookUrl != null ? webhookUrl : properties.webhookUrl())
                    .setToken(clientStateSecret);
            Channel response = calendar.events().watch(PRIMARY_CALENDAR, channel).execute();
            Instant expiry = response.getExpiration() != null ? Instant.ofEpochMilli(response.getExpiration()) : null;
            return new WatchRegistration(response.getId(), response.getResourceId(), expiry);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot register Google Calendar watch", e);
        }
    }

    private ExternalCalendarEvent toExternalEvent(Event event) {
        Instant start = toInstant(event.getStart());
        Instant end = toInstant(event.getEnd());
        return new ExternalCalendarEvent(event.getId(), event.getSummary(), start, end, event.getEtag());
    }

    private Instant toInstant(EventDateTime dateTime) {
        if (dateTime == null) return null;
        DateTime value = dateTime.getDateTime() != null ? dateTime.getDateTime() : dateTime.getDate();
        return value != null ? Instant.ofEpochMilli(value.getValue()) : null;
    }

    private EventDateTime toEventDateTime(Instant instant) {
        return new EventDateTime().setDateTime(new DateTime(instant.toEpochMilli()));
    }
}
