package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.exception.EmailMessageNotFoundException;
import com.gresk.modules.email.domain.model.EmailEntityRecord;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.domain.model.EmailProcessingResult;
import com.gresk.modules.email.domain.model.EventContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.model.RiderDiff;
import com.gresk.modules.email.domain.model.RiderVersionSource;
import com.gresk.modules.email.domain.port.out.EmailDraftReplyRepositoryPort;
import com.gresk.modules.email.domain.port.out.EmailEntityRecordRepositoryPort;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.email.domain.port.out.EmailRiderVersionRepositoryPort;
import com.gresk.modules.email.domain.port.out.EventLinkerPort;
import com.gresk.modules.email.domain.service.DraftGenerationService;
import com.gresk.modules.email.domain.service.EmailClassificationPipeline;
import com.gresk.modules.email.domain.service.RiderVersioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Procesa un correo ya ingerido: lo vincula a evento si el hilo es conocido,
 * ejecuta el pipeline híbrido de clasificación y persiste el resultado
 * (clasificación, entidades extraídas y borrador sugerido si lo hay).
 * Si el pipeline falla, el correo queda FAILED (o DEAD_LETTER al agotar
 * reintentos) para ser reprocesado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProcessEmailUseCase {

    private final EmailMessageRepositoryPort      emailRepository;
    private final EmailEntityRecordRepositoryPort entityRepository;
    private final EmailDraftReplyRepositoryPort   draftRepository;
    private final EmailRiderVersionRepositoryPort riderVersionRepository;
    private final EventLinkerPort                 eventLinker;
    private final EmailClassificationPipeline     pipeline;
    private final DraftGenerationService          draftGenerationService;
    private final RiderVersioningService          riderVersioningService;
    private final ObjectMapper                    objectMapper;

    @Value("${gresk.email.ai.max-attempts:3}")
    private int maxAttempts;

    public void execute(UUID emailId) {
        EmailMessage email = emailRepository.findById(EmailMessageId.of(emailId))
                .orElseThrow(() -> new EmailMessageNotFoundException(EmailMessageId.of(emailId)));

        if (email.isProcessed()) {
            log.debug("Email {} already processed, skipping", emailId);
            return;
        }

        email.markProcessing();

        try {
            if (email.getEventId() == null) {
                eventLinker.findEventForThread(email.getPromoterId(), email.getExternalThreadId())
                        .ifPresent(email::linkToEvent);
            }

            EventContext context = email.getEventId() != null
                    ? EventContext.forThread(email.getEventId(), email.getExternalThreadId())
                    : EventContext.empty();

            EmailProcessingResult result = pipeline.process(email, context);

            persistEntities(email, result);
            createRiderVersionIfPresent(email, result);
            draftGenerationService.generateFrom(email, result).ifPresent(draftRepository::save);

            email.completeProcessing(result.classification());
            log.info("Email {} classified as {} ({}, confidence {})", emailId,
                    result.classification().classification(),
                    result.classification().source(),
                    result.classification().confidence());

        } catch (Exception e) {
            email.markFailed(maxAttempts);
            log.error("Processing failed for email {} (attempt {}, status {}): {}", emailId,
                    email.getProcessingAttempts(), email.getProcessingStatus(), e.getMessage());
        }

        emailRepository.save(email);
    }

    /**
     * Si Claude extrajo un rider y el correo está vinculado a un evento,
     * crea la siguiente versión con su diff respecto a la anterior.
     */
    private void createRiderVersionIfPresent(EmailMessage email, EmailProcessingResult result) {
        if (!result.hasRiderData() || email.getEventId() == null) {
            return;
        }
        try {
            var latest = riderVersionRepository.findLatestByEventId(email.getEventId());

            RiderDiff diff = latest
                    .map(prev -> riderVersioningService.computeDiff(
                            parseRiderData(prev.getRiderDataJson()), result.riderData()))
                    .orElse(null);

            riderVersionRepository.save(EmailRiderVersion.create(
                    email.getEventId(),
                    riderVersioningService.nextVersionNumber(latest),
                    email.getId(),
                    objectMapper.writeValueAsString(result.riderData()),
                    diff != null ? objectMapper.writeValueAsString(diff) : null,
                    RiderVersionSource.AI,
                    null));
        } catch (Exception e) {
            // El rider no debe tumbar el procesamiento completo del email
            log.error("Could not create rider version for email {}: {}",
                    email.getId(), e.getMessage());
        }
    }

    private java.util.Map<String, Object> parseRiderData(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return java.util.Map.of();
        }
    }

    private void persistEntities(EmailMessage email, EmailProcessingResult result) {
        List<EmailEntityRecord> records = result.entities().stream()
                .map(e -> EmailEntityRecord.create(
                        email.getId(),
                        e.type(),
                        e.key(),
                        e.value(),
                        e.normalizedValueJson(),
                        BigDecimal.valueOf(e.confidence()).setScale(2, RoundingMode.HALF_UP),
                        e.sourceSnippet(),
                        e.requiresAction()))
                .toList();
        if (!records.isEmpty()) {
            entityRepository.saveAll(records);
        }
    }
}
