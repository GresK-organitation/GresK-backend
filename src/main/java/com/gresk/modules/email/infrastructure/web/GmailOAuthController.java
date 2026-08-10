package com.gresk.modules.email.infrastructure.web;

import com.gresk.modules.email.domain.model.PromoterGmailToken;
import com.gresk.modules.email.infrastructure.gmail.GmailOAuthService;
import com.gresk.modules.email.infrastructure.gmail.GmailSyncService;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Flujo OAuth de Gmail. connect/disconnect requieren rol PROMOTER;
 * el callback lo invoca el navegador redirigido por Google (sin JWT),
 * protegido por el state anti-CSRF de un solo uso.
 */
@RestController
@RequestMapping("/api/v1/email/gmail")
@RequiredArgsConstructor
public class GmailOAuthController {

    private final GmailOAuthService oauthService;
    private final GmailSyncService  syncService;

    @GetMapping("/connect")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Map<String, String>> connect(@AuthenticationPrincipal String promoterId) {
        String authUrl = oauthService.buildAuthorizationUrl(PromoterId.of(promoterId));
        return ResponseEntity.ok(Map.of("authorizationUrl", authUrl));
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> callback(@RequestParam("code") String code,
                                                        @RequestParam("state") String state) {
        PromoterGmailToken token = oauthService.handleCallback(code, state);
        return ResponseEntity.ok(Map.of(
                "status", "connected",
                "gmailAddress", token.getGmailAddress() != null ? token.getGmailAddress() : ""));
    }

    @DeleteMapping("/disconnect")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> disconnect(@AuthenticationPrincipal String promoterId) {
        oauthService.disconnect(PromoterId.of(promoterId));
        return ResponseEntity.noContent().build();
    }

    /** Ingesta manual de los N emails más recientes — solo para dev/test (sin Pub/Sub). */
    @PostMapping("/sync-now")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Map<String, Object>> syncNow(
            @AuthenticationPrincipal String promoterId,
            @RequestParam(defaultValue = "50") int max) {
        int ingested = syncService.syncRecent(PromoterId.of(promoterId), max);
        return ResponseEntity.ok(Map.of("ingested", ingested));
    }
}
