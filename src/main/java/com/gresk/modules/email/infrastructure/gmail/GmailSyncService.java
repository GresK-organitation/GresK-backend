package com.gresk.modules.email.infrastructure.gmail;

import com.google.api.services.gmail.Gmail;
import com.gresk.modules.email.application.command.IngestEmailCommand;
import com.gresk.modules.email.application.usecase.IngestEmailUseCase;
import com.gresk.modules.email.domain.model.PromoterGmailToken;
import com.gresk.modules.email.domain.port.out.PromoterGmailTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.math.BigInteger;
import java.util.List;

/**
 * Sincronización incremental tras una notificación Pub/Sub: descarga los
 * mensajes añadidos desde el último historyId conocido y los ingesta
 * (la deduplicación del ingest hace el proceso idempotente).
 * Asíncrono: el webhook debe responder a Google de inmediato.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GmailSyncService {

    private final PromoterGmailTokenRepositoryPort tokenRepository;
    private final GmailApiAdapter                  gmailApiAdapter;
    private final IngestEmailUseCase               ingestEmailUseCase;

    /** Ingesta los N emails más recientes del promoter sin necesitar historyId (dev/test). */
    public int syncRecent(PromoterId promoterId, int maxMessages) {
        return tokenRepository.findByPromoterId(promoterId)
                .map(token -> {
                    Gmail gmail = gmailApiAdapter.clientFor(token);
                    List<String> ids = gmailApiAdapter.listRecentMessageIds(gmail, maxMessages);
                    int count = 0;
                    for (String messageId : ids) {
                        try {
                            IngestEmailCommand cmd = gmailApiAdapter.fetchMessage(
                                    gmail, token.getPromoterId().value(), messageId);
                            ingestEmailUseCase.execute(cmd);
                            count++;
                        } catch (Exception e) {
                            log.error("Manual sync: failed to ingest message {}: {}", messageId, e.getMessage());
                        }
                    }
                    log.info("Manual sync for promoter {}: {}/{} messages ingested",
                            promoterId.value(), count, ids.size());
                    return count;
                })
                .orElseThrow(() -> new IllegalStateException("No Gmail token linked for this promoter"));
    }

    @Async
    public void sync(String emailAddress, Long notifiedHistoryId) {
        tokenRepository.findByGmailAddress(emailAddress).ifPresentOrElse(
                token -> syncMailbox(token, notifiedHistoryId),
                () -> log.warn("Pub/Sub notification for unknown Gmail address {}", emailAddress));
    }

    private void syncMailbox(PromoterGmailToken token, Long notifiedHistoryId) {
        try {
            Long startHistoryId = token.getLastHistoryId() != null
                    ? token.getLastHistoryId()
                    : notifiedHistoryId;

            Gmail gmail = gmailApiAdapter.clientFor(token);
            List<String> messageIds = gmailApiAdapter.listNewMessageIds(
                    gmail, BigInteger.valueOf(startHistoryId));

            for (String messageId : messageIds) {
                try {
                    IngestEmailCommand cmd = gmailApiAdapter.fetchMessage(
                            gmail, token.getPromoterId().value(), messageId);
                    ingestEmailUseCase.execute(cmd);
                } catch (Exception e) {
                    log.error("Failed to ingest Gmail message {}: {}", messageId, e.getMessage());
                }
            }

            token.trackHistory(notifiedHistoryId);
            tokenRepository.save(token);
            log.info("Gmail sync for {}: {} new messages", token.getGmailAddress(), messageIds.size());

        } catch (Exception e) {
            // El historyId puede haber expirado (Gmail lo conserva ~1 semana):
            // se avanza el cursor y la siguiente notificación sincronizará desde ahí
            log.error("Gmail sync failed for {}: {}", token.getGmailAddress(), e.getMessage());
            token.trackHistory(notifiedHistoryId);
            tokenRepository.save(token);
        }
    }
}
