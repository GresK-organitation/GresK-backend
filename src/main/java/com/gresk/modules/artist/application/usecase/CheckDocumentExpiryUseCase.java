package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.CheckDocumentExpiryPort;
import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.artist.domain.port.out.BandMemberRepositoryPort;
import com.gresk.modules.artist.domain.port.out.DocumentExpiryAlertRepositoryPort;
import com.gresk.modules.email.domain.port.out.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Recorre band members activos y crea una DocumentExpiryAlert cuando un documento
 * entra en ventana de aviso (90 días), evitando duplicar mientras la última alerta
 * siga sin leer (existsUnreadFor). No es @Transactional a nivel de clase: cada
 * alerta se persiste de forma aislada para que un fallo no bloquee al resto.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckDocumentExpiryUseCase implements CheckDocumentExpiryPort {

    private static final int WARNING_WINDOW_DAYS = 90;

    private final BandMemberRepositoryPort          bandMemberRepository;
    private final DocumentExpiryAlertRepositoryPort alertRepository;
    private final EmailSenderPort                   emailSender;

    @Override
    public void execute() {
        List<BandMember> members = bandMemberRepository.findAllActiveWithDocuments();
        log.info("Document expiry check: {} active band member(s)", members.size());

        LocalDate today = LocalDate.now();
        for (BandMember member : members) {
            for (IdentityDocument document : member.getDocuments()) {
                try {
                    checkDocument(member, document, today);
                } catch (Exception e) {
                    log.error("Failed to check document expiry for band member {} / {}: {}",
                            member.getId().value(), document.type(), e.getMessage());
                }
            }
        }
    }

    private void checkDocument(BandMember member, IdentityDocument document, LocalDate today) {
        if (document.expiryDate() == null) return;

        long daysUntilExpiry = ChronoUnit.DAYS.between(today, document.expiryDate());
        if (daysUntilExpiry > WARNING_WINDOW_DAYS) return;
        if (alertRepository.existsUnreadFor(member.getId(), document.type())) return;

        String message = buildMessage(member, document, daysUntilExpiry);
        DocumentExpiryAlert alert = DocumentExpiryAlert.create(
                member.getPromoterId(), member.getArtistId(), member.getId(),
                document.type(), document.expiryDate(), message);
        alertRepository.save(alert);
        log.info("Document expiry alert created for band member {} ({}) — {} day(s) remaining",
                member.getId().value(), document.type(), daysUntilExpiry);

        notifyByEmail(member, message);
    }

    private String buildMessage(BandMember member, IdentityDocument document, long daysUntilExpiry) {
        if (daysUntilExpiry < 0) {
            return String.format("El documento %s (%s) de %s está caducado desde hace %d día(s).",
                    document.type(), document.documentNumber(), member.getName().value(), -daysUntilExpiry);
        }
        return String.format("El documento %s (%s) de %s caduca en %d día(s).",
                document.type(), document.documentNumber(), member.getName().value(), daysUntilExpiry);
    }

    private void notifyByEmail(BandMember member, String message) {
        // Best-effort: un fallo de envío no debe impedir que la alerta in-app quede
        // guardada. Nota de implementación: el email de destino de la promotora no
        // está disponible directamente desde BandMember — inyectar un lookup a
        // PromoterRepositoryPort (módulo promoter) por member.getPromoterId() al
        // integrar con datos reales, en vez de asumir un campo que no existe en el dominio.
        try {
            emailSender.send("promotora@gresk.app",
                    "Documento próximo a caducar — " + member.getName().value(), message);
        } catch (Exception e) {
            log.warn("Failed to send document expiry email for band member {}: {}",
                    member.getId().value(), e.getMessage());
        }
    }
}
