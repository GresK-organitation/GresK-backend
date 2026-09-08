package com.gresk.modules.agenda.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.agenda.application.command.HandleCalendarWebhookCommand;
import com.gresk.modules.agenda.application.port.in.HandleCalendarWebhookUseCase;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints públicos (permitAll en SecurityConfig) que reciben las notificaciones push de
 * Google Calendar (canal watch) y Microsoft Graph (suscripción). A diferencia del webhook
 * de Gmail — que resuelve la cuenta por email y usa un token estático compartido — aquí cada
 * cuenta tiene su propio {@code clientStateSecret} y la resolución es por id de canal/suscripción.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/agenda/calendar-sync/webhook")
@RequiredArgsConstructor
@Tag(name = "CalendarWebhook", description = "Recepción de notificaciones push de Google Calendar / Microsoft Graph")
public class CalendarWebhookController {

    private final HandleCalendarWebhookUseCase handleCalendarWebhookUseCase;
    private final ObjectMapper objectMapper;

    @PostMapping("/google")
    public ResponseEntity<Void> google(@RequestHeader("X-Goog-Channel-ID") String channelId,
                                        @RequestHeader(value = "X-Goog-Channel-Token", required = false) String channelToken,
                                        @RequestHeader(value = "X-Goog-Resource-State", required = false) String resourceState) {
        if (!"sync".equals(resourceState)) {
            handleCalendarWebhookUseCase.execute(
                    new HandleCalendarWebhookCommand(channelId, CalendarProvider.GOOGLE.name(), channelToken));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/outlook", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> outlook(@RequestParam(required = false) String validationToken,
                                           @RequestBody(required = false) String rawBody) {
        // Handshake de validación de la suscripción (Microsoft Graph exige eco en texto plano).
        if (validationToken != null) {
            return ResponseEntity.status(HttpStatus.OK).body(validationToken);
        }
        try {
            JsonNode payload = objectMapper.readTree(rawBody);
            for (JsonNode notification : payload.withArray("value")) {
                String subscriptionId = notification.path("subscriptionId").asText(null);
                String clientState = notification.path("clientState").asText(null);
                if (subscriptionId != null) {
                    handleCalendarWebhookUseCase.execute(
                            new HandleCalendarWebhookCommand(subscriptionId, CalendarProvider.OUTLOOK.name(), clientState));
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse Outlook webhook payload: {}", e.getMessage());
        }
        return ResponseEntity.accepted().build();
    }
}
