package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Estado anti-CSRF del flujo OAuth2 de calendario, en memoria con TTL — mismo patrón que
 * {@code GmailOAuthService.pendingStates}. Límite conocido: no sobrevive a un reinicio ni
 * se comparte entre instancias; aceptable mientras el despliegue sea de instancia única.
 */
@Component
public class CalendarOAuthStateStore {

    private static final long STATE_TTL_SECONDS = 600;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, PendingState> pendingStates = new ConcurrentHashMap<>();

    public String issue(PromoterId promoterId, CalendarProvider provider) {
        purgeExpired();
        byte[] nonce = new byte[24];
        random.nextBytes(nonce);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(nonce);
        pendingStates.put(state, new PendingState(promoterId, provider, Instant.now().plusSeconds(STATE_TTL_SECONDS)));
        return state;
    }

    public PendingState consume(String state) {
        PendingState pending = pendingStates.remove(state);
        if (pending == null || pending.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired OAuth state");
        }
        return pending;
    }

    private void purgeExpired() {
        pendingStates.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public record PendingState(PromoterId promoterId, CalendarProvider provider, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
