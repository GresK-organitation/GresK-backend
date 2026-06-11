package com.gresk.modules.email.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.infrastructure.gmail.GmailProperties;
import com.gresk.modules.email.infrastructure.gmail.GmailSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

/**
 * Push endpoint de Gmail Pub/Sub. Google envía un POST con el envelope
 * {"message": {"data": base64({"emailAddress", "historyId"})}}.
 * Se valida un token compartido (query param) y se responde rápido:
 * la sincronización real es asíncrona.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/email/gmail/webhook")
@RequiredArgsConstructor
public class EmailWebhookController {

    private final GmailProperties  properties;
    private final GmailSyncService syncService;
    private final ObjectMapper     objectMapper;

    @PostMapping
    public ResponseEntity<Void> receive(@RequestParam(value = "token", required = false) String token,
                                        @RequestBody PubSubPushRequest request) {
        if (!isValidToken(token)) {
            log.warn("Gmail webhook rejected: invalid Pub/Sub verification token");
            return ResponseEntity.status(401).build();
        }

        try {
            JsonNode payload = objectMapper.readTree(
                    Base64.getDecoder().decode(request.message().data()));
            String emailAddress = payload.path("emailAddress").asText(null);
            Long historyId = payload.hasNonNull("historyId")
                    ? payload.get("historyId").asLong()
                    : null;

            if (emailAddress == null || historyId == null) {
                log.warn("Gmail webhook: malformed notification payload");
            } else {
                syncService.sync(emailAddress, historyId);
            }
        } catch (Exception e) {
            // Se responde 2xx igualmente: un error de parseo no se arregla
            // con las redeliveries infinitas de Pub/Sub
            log.error("Gmail webhook: cannot decode notification: {}", e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

    private boolean isValidToken(String token) {
        return !properties.pubsubVerificationToken().isBlank()
                && properties.pubsubVerificationToken().equals(token);
    }

    public record PubSubPushRequest(PubSubMessage message, String subscription) {
        public record PubSubMessage(String data, String messageId) {}
    }
}
