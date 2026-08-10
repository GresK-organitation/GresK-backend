package com.gresk.modules.email.application.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.domain.exception.EventNotOwnedException;
import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.model.RiderDiff;
import com.gresk.modules.email.domain.model.RiderVersionSource;
import com.gresk.modules.email.domain.port.out.EmailRiderVersionRepositoryPort;
import com.gresk.modules.email.domain.port.out.EventInfoPort;
import com.gresk.modules.email.domain.service.RiderVersioningService;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Edición manual del rider: crea la siguiente versión con origen MANUAL. */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateManualRiderVersionUseCase {

    private final EmailRiderVersionRepositoryPort repository;
    private final RiderVersioningService          versioningService;
    private final EventInfoPort                   eventInfo;
    private final ObjectMapper                    objectMapper;

    public EmailRiderVersion execute(UUID eventId, UUID promoterId,
                                     Map<String, Object> riderData, String notes) {
        eventInfo.findEventTitle(eventId, PromoterId.of(promoterId))
                .orElseThrow(() -> new EventNotOwnedException(eventId));

        if (riderData == null || riderData.isEmpty()) {
            throw new IllegalArgumentException("riderData must not be empty");
        }

        try {
            Optional<EmailRiderVersion> latest = repository.findLatestByEventId(eventId);

            RiderDiff diff = latest
                    .map(prev -> versioningService.computeDiff(
                            parseRiderData(prev.getRiderDataJson()), riderData))
                    .orElse(null);

            return repository.save(EmailRiderVersion.create(
                    eventId,
                    versioningService.nextVersionNumber(latest),
                    null,
                    objectMapper.writeValueAsString(riderData),
                    diff != null ? objectMapper.writeValueAsString(diff) : null,
                    RiderVersionSource.MANUAL,
                    notes));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Could not create manual rider version", e);
        }
    }

    private Map<String, Object> parseRiderData(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
