package com.gresk.modules.email.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Correo entrante sincronizado desde Gmail (aggregate root).
 * La clasificación y extracción de entidades se rellenan en fases
 * posteriores del pipeline (Issues #2-#3 del EIE).
 */
public final class EmailMessage {

    private final EmailMessageId id;
    private final PromoterId     promoterId;
    private final String         externalMessageId;
    private final Instant        receivedAt;
    private final Instant        createdAt;

    private UUID                eventId;
    private String              externalThreadId;
    private String              fromAddress;
    private String              fromName;
    private List<String>        toAddresses;
    private String              subject;
    private String              bodyText;
    private String              bodyHtml;
    private String              rawHeadersJson;
    private EmailClassification classification;
    private BigDecimal          classificationConfidence;
    private ProcessingStatus    processingStatus;
    private int                 processingAttempts;
    private Instant             lastAttemptAt;
    private Instant             processedAt;

    private EmailMessage(EmailMessageId id, PromoterId promoterId, UUID eventId,
                         String externalMessageId, String externalThreadId,
                         String fromAddress, String fromName, List<String> toAddresses,
                         String subject, String bodyText, String bodyHtml, String rawHeadersJson,
                         EmailClassification classification, BigDecimal classificationConfidence,
                         ProcessingStatus processingStatus, int processingAttempts,
                         Instant lastAttemptAt, Instant processedAt,
                         Instant receivedAt, Instant createdAt) {
        this.id                       = id;
        this.promoterId               = promoterId;
        this.eventId                  = eventId;
        this.externalMessageId        = externalMessageId;
        this.externalThreadId         = externalThreadId;
        this.fromAddress              = fromAddress;
        this.fromName                 = fromName;
        this.toAddresses              = toAddresses != null ? new ArrayList<>(toAddresses) : new ArrayList<>();
        this.subject                  = subject;
        this.bodyText                 = bodyText;
        this.bodyHtml                 = bodyHtml;
        this.rawHeadersJson           = rawHeadersJson;
        this.classification           = classification;
        this.classificationConfidence = classificationConfidence;
        this.processingStatus         = processingStatus;
        this.processingAttempts       = processingAttempts;
        this.lastAttemptAt            = lastAttemptAt;
        this.processedAt              = processedAt;
        this.receivedAt               = receivedAt;
        this.createdAt                = createdAt;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    /** Correo recién ingerido: entra al pipeline en estado PENDING. */
    public static EmailMessage receive(PromoterId promoterId, String externalMessageId,
                                       String externalThreadId, String fromAddress, String fromName,
                                       List<String> toAddresses, String subject,
                                       String bodyText, String bodyHtml, String rawHeadersJson,
                                       Instant receivedAt) {
        return new EmailMessage(
                EmailMessageId.generate(), promoterId, null,
                externalMessageId, externalThreadId,
                fromAddress, fromName, toAddresses,
                subject, bodyText, bodyHtml, rawHeadersJson,
                null, null,
                ProcessingStatus.PENDING, 0,
                null, null,
                receivedAt, Instant.now()
        );
    }

    public static EmailMessage reconstitute(
            EmailMessageId id, PromoterId promoterId, UUID eventId,
            String externalMessageId, String externalThreadId,
            String fromAddress, String fromName, List<String> toAddresses,
            String subject, String bodyText, String bodyHtml, String rawHeadersJson,
            EmailClassification classification, BigDecimal classificationConfidence,
            ProcessingStatus processingStatus, int processingAttempts,
            Instant lastAttemptAt, Instant processedAt,
            Instant receivedAt, Instant createdAt) {
        return new EmailMessage(id, promoterId, eventId,
                externalMessageId, externalThreadId,
                fromAddress, fromName, toAddresses,
                subject, bodyText, bodyHtml, rawHeadersJson,
                classification, classificationConfidence,
                processingStatus, processingAttempts,
                lastAttemptAt, processedAt,
                receivedAt, createdAt);
    }

    // ── Transiciones del pipeline ─────────────────────────────────────────────

    public void linkToEvent(UUID eventId) {
        this.eventId = eventId;
    }

    /** El worker toma el correo: cuenta el intento y lo marca en curso. */
    public void markProcessing() {
        this.processingStatus = ProcessingStatus.PROCESSING;
        this.processingAttempts++;
        this.lastAttemptAt = Instant.now();
    }

    public void completeProcessing(ClassificationResult result) {
        this.classification           = result.classification();
        this.classificationConfidence = BigDecimal.valueOf(result.confidence())
                                                  .setScale(2, RoundingMode.HALF_UP);
        this.processingStatus = ProcessingStatus.DONE;
        this.processedAt      = Instant.now();
    }

    /** Marca el intento como fallido; pasa a DEAD_LETTER si se agotaron los reintentos. */
    public void markFailed(int maxAttempts) {
        this.processingStatus = processingAttempts >= maxAttempts
                ? ProcessingStatus.DEAD_LETTER
                : ProcessingStatus.FAILED;
    }

    public boolean isProcessed() {
        return processingStatus == ProcessingStatus.DONE;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public EmailMessageId getId()                         { return id; }
    public PromoterId getPromoterId()                     { return promoterId; }
    public UUID getEventId()                              { return eventId; }
    public String getExternalMessageId()                  { return externalMessageId; }
    public String getExternalThreadId()                   { return externalThreadId; }
    public String getFromAddress()                        { return fromAddress; }
    public String getFromName()                           { return fromName; }
    public List<String> getToAddresses()                  { return List.copyOf(toAddresses); }
    public String getSubject()                            { return subject; }
    public String getBodyText()                           { return bodyText; }
    public String getBodyHtml()                           { return bodyHtml; }
    public String getRawHeadersJson()                     { return rawHeadersJson; }
    public EmailClassification getClassification()        { return classification; }
    public BigDecimal getClassificationConfidence()       { return classificationConfidence; }
    public ProcessingStatus getProcessingStatus()         { return processingStatus; }
    public int getProcessingAttempts()                    { return processingAttempts; }
    public Instant getLastAttemptAt()                     { return lastAttemptAt; }
    public Instant getProcessedAt()                       { return processedAt; }
    public Instant getReceivedAt()                        { return receivedAt; }
    public Instant getCreatedAt()                         { return createdAt; }
}
