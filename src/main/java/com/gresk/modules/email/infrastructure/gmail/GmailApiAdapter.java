package com.gresk.modules.email.infrastructure.gmail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.gresk.modules.email.application.command.IngestEmailCommand;
import com.gresk.modules.email.domain.model.PromoterGmailToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * Acceso a la API de Gmail: descarga de mensajes, historial incremental
 * y registro del watch Pub/Sub. Las credenciales se refrescan solas
 * (UserCredentials + refresh token).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GmailApiAdapter {

    private static final String ME = "me";

    private final GmailProperties properties;
    private final ObjectMapper    objectMapper;

    public Gmail clientFor(PromoterGmailToken token) {
        try {
            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(properties.clientId())
                    .setClientSecret(properties.clientSecret())
                    .setRefreshToken(token.getRefreshToken())
                    .setAccessToken(new AccessToken(
                            token.getAccessToken(),
                            token.getTokenExpiry() != null ? Date.from(token.getTokenExpiry()) : null))
                    .build();

            return new Gmail.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("GresK")
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build Gmail client", e);
        }
    }

    /** Descarga un email por messageId y lo convierte en comando de ingesta. */
    public IngestEmailCommand fetchMessage(Gmail gmail, UUID promoterId, String messageId) {
        try {
            Message message = gmail.users().messages().get(ME, messageId).setFormat("full").execute();
            MessagePart payload = message.getPayload();
            Map<String, String> headers = headerMap(payload);

            ParsedAddress from = ParsedAddress.parse(headers.get("from"));
            List<String> to = headers.get("to") != null
                    ? Arrays.stream(headers.get("to").split(",")).map(String::trim).toList()
                    : List.of();

            return new IngestEmailCommand(
                    promoterId,
                    message.getId(),
                    message.getThreadId(),
                    from.address(),
                    from.name(),
                    to,
                    headers.get("subject"),
                    extractBody(payload, "text/plain"),
                    extractBody(payload, "text/html"),
                    objectMapper.writeValueAsString(headers),
                    message.getInternalDate() != null
                            ? Instant.ofEpochMilli(message.getInternalDate())
                            : Instant.now()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Cannot fetch Gmail message " + messageId, e);
        }
    }

    /** Ids de los N mensajes más recientes (sin historyId — para sync manual en dev). */
    public List<String> listRecentMessageIds(Gmail gmail, int maxResults) {
        try {
            List<com.google.api.services.gmail.model.Message> messages =
                    gmail.users().messages().list(ME)
                            .setMaxResults((long) maxResults)
                            .execute()
                            .getMessages();
            return messages == null ? List.of()
                    : messages.stream()
                              .map(com.google.api.services.gmail.model.Message::getId)
                              .toList();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot list recent Gmail messages", e);
        }
    }

    /** Ids de mensajes añadidos desde startHistoryId (sync incremental). */
    public List<String> listNewMessageIds(Gmail gmail, BigInteger startHistoryId) {
        try {
            List<String> ids = new ArrayList<>();
            String pageToken = null;
            do {
                ListHistoryResponse response = gmail.users().history().list(ME)
                        .setStartHistoryId(startHistoryId)
                        .setHistoryTypes(List.of("messageAdded"))
                        .setPageToken(pageToken)
                        .execute();
                for (History history : Optional.ofNullable(response.getHistory()).orElse(List.of())) {
                    for (HistoryMessageAdded added :
                            Optional.ofNullable(history.getMessagesAdded()).orElse(List.of())) {
                        ids.add(added.getMessage().getId());
                    }
                }
                pageToken = response.getNextPageToken();
            } while (pageToken != null);
            return ids;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot list Gmail history from " + startHistoryId, e);
        }
    }

    /** Registra el watch Pub/Sub del buzón; devuelve (historyId, expiración). */
    public WatchResult watchMailbox(Gmail gmail) {
        try {
            WatchResponse response = gmail.users().watch(ME,
                    new WatchRequest().setTopicName(properties.pubsubTopic())).execute();
            return new WatchResult(
                    response.getHistoryId() != null ? response.getHistoryId().longValue() : null,
                    response.getExpiration() != null
                            ? Instant.ofEpochMilli(response.getExpiration())
                            : null);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot register Gmail watch", e);
        }
    }

    public void stopWatch(Gmail gmail) {
        try {
            gmail.users().stop(ME).execute();
        } catch (Exception e) {
            log.warn("Could not stop Gmail watch: {}", e.getMessage());
        }
    }

    public String profileEmailAddress(Gmail gmail) {
        try {
            return gmail.users().getProfile(ME).execute().getEmailAddress();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read Gmail profile", e);
        }
    }

    public record WatchResult(Long historyId, Instant expiration) {}

    // ── Helpers de parseo MIME ────────────────────────────────────────────────

    private Map<String, String> headerMap(MessagePart payload) {
        Map<String, String> headers = new LinkedHashMap<>();
        if (payload != null && payload.getHeaders() != null) {
            for (MessagePartHeader header : payload.getHeaders()) {
                headers.putIfAbsent(header.getName().toLowerCase(), header.getValue());
            }
        }
        return headers;
    }

    private String extractBody(MessagePart part, String mimeType) {
        if (part == null) return null;
        if (mimeType.equals(part.getMimeType()) && part.getBody() != null
                && part.getBody().getData() != null) {
            return new String(Base64.getUrlDecoder().decode(part.getBody().getData()),
                    StandardCharsets.UTF_8);
        }
        if (part.getParts() != null) {
            for (MessagePart child : part.getParts()) {
                String body = extractBody(child, mimeType);
                if (body != null) return body;
            }
        }
        return null;
    }

    /** "Nombre Apellido <correo@dominio>" → (correo@dominio, Nombre Apellido) */
    record ParsedAddress(String address, String name) {
        static ParsedAddress parse(String header) {
            if (header == null || header.isBlank()) return new ParsedAddress("unknown", null);
            int lt = header.indexOf('<');
            int gt = header.indexOf('>');
            if (lt >= 0 && gt > lt) {
                String name = header.substring(0, lt).trim().replaceAll("^\"|\"$", "");
                return new ParsedAddress(header.substring(lt + 1, gt).trim(),
                        name.isBlank() ? null : name);
            }
            return new ParsedAddress(header.trim(), null);
        }
    }
}
