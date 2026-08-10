package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.application.dto.EventEmailSummary;
import com.gresk.modules.email.domain.exception.EventNotOwnedException;
import com.gresk.modules.email.domain.model.*;
import com.gresk.modules.email.domain.port.out.*;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * Resumen agregado de las comunicaciones de un evento: conteos por
 * clasificación, entidades clave, acciones pendientes, rider activo y
 * borradores por revisar.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEventEmailSummaryUseCase {

    private static final Set<ExtractedEntityType> KEY_ENTITY_TYPES = Set.of(
            ExtractedEntityType.AMOUNT, ExtractedEntityType.DATE,
            ExtractedEntityType.ARTIST_NAME, ExtractedEntityType.VENUE_NAME,
            ExtractedEntityType.CONTACT_NAME);

    private final EmailMessageRepositoryPort      emailRepository;
    private final EmailEntityRecordRepositoryPort entityRepository;
    private final EmailRiderVersionRepositoryPort riderVersionRepository;
    private final EmailDraftReplyRepositoryPort   draftRepository;
    private final EventInfoPort                   eventInfo;

    public EventEmailSummary execute(UUID eventId, UUID promoterId) {
        PromoterId promoter = PromoterId.of(promoterId);

        String eventName = eventInfo.findEventTitle(eventId, promoter)
                .orElseThrow(() -> new EventNotOwnedException(eventId));

        List<EmailMessage> emails = emailRepository.findByEventIdAndPromoterId(eventId, promoter);
        Set<UUID> emailIds = new HashSet<>();
        Map<String, Integer> classificationCounts = new LinkedHashMap<>();
        for (EmailMessage email : emails) {
            emailIds.add(email.getId().value());
            if (email.getClassification() != null) {
                classificationCounts.merge(email.getClassification().name(), 1, Integer::sum);
            }
        }

        List<EmailEntityRecord> entities = entityRepository.findByEmailIds(emailIds);
        int pendingActions = 0;
        Map<String, String> keyEntities = new LinkedHashMap<>();
        List<String> riderPendingItems = new ArrayList<>();
        for (EmailEntityRecord entity : entities) {
            boolean pending = entity.isRequiresAction() && entity.getActionedAt() == null;
            if (pending) {
                pendingActions++;
                if (entity.getEntityType() == ExtractedEntityType.RIDER_ITEM) {
                    riderPendingItems.add(entity.getEntityValue());
                }
            }
            if (KEY_ENTITY_TYPES.contains(entity.getEntityType())) {
                String key = entity.getEntityKey() != null
                        ? entity.getEntityKey()
                        : entity.getEntityType().name().toLowerCase();
                keyEntities.put(key, entity.getEntityValue());
            }
        }

        Optional<EmailRiderVersion> rider = riderVersionRepository.findLatestByEventId(eventId);

        int pendingDrafts = (int) draftRepository
                .findByPromoterIdAndStatus(promoter, DraftReplyStatus.PENDING_REVIEW).stream()
                .filter(draft -> emailIds.contains(draft.getEmailId().value()))
                .count();

        return new EventEmailSummary(
                eventId,
                eventName,
                emails.size(),
                emails.isEmpty() ? null : emails.get(0).getReceivedAt(),
                pendingActions,
                classificationCounts,
                keyEntities,
                rider.map(EmailRiderVersion::getVersionNumber).orElse(null),
                rider.map(EmailRiderVersion::getCreatedAt).orElse(null),
                riderPendingItems,
                pendingDrafts
        );
    }
}
