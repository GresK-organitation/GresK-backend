package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.model.valueobject.DocumentExpiryAlertId;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Alerta de caducidad de un documento de identidad de un BandMember.
 * Mismo patrón que RiderAlert: entidad ligera con repositorio propio,
 * generada por un scheduler, marcable como leída.
 */
public final class DocumentExpiryAlert {

    private final DocumentExpiryAlertId id;
    private final PromoterId            promoterId;
    private final ArtistId              artistId;
    private final BandMemberId          bandMemberId;
    private final IdentityDocumentType  documentType;
    private final LocalDate             expiryDate;
    private final String                message;
    private boolean                     read;
    private final Instant               createdAt;

    private DocumentExpiryAlert(DocumentExpiryAlertId id, PromoterId promoterId, ArtistId artistId,
                                 BandMemberId bandMemberId, IdentityDocumentType documentType,
                                 LocalDate expiryDate, String message, boolean read, Instant createdAt) {
        this.id           = Objects.requireNonNull(id, "DocumentExpiryAlertId is required");
        this.promoterId   = Objects.requireNonNull(promoterId, "PromoterId is required");
        this.artistId     = Objects.requireNonNull(artistId, "ArtistId is required");
        this.bandMemberId = Objects.requireNonNull(bandMemberId, "BandMemberId is required");
        this.documentType = Objects.requireNonNull(documentType, "IdentityDocumentType is required");
        this.expiryDate   = Objects.requireNonNull(expiryDate, "expiryDate is required");
        this.message      = message;
        this.read         = read;
        this.createdAt    = Objects.requireNonNull(createdAt, "CreatedAt is required");
    }

    // ── Factories ──────────────────────────────────────────────────────────────

    public static DocumentExpiryAlert create(PromoterId promoterId, ArtistId artistId, BandMemberId bandMemberId,
                                              IdentityDocumentType documentType, LocalDate expiryDate, String message) {
        return new DocumentExpiryAlert(DocumentExpiryAlertId.generate(), promoterId, artistId, bandMemberId,
                documentType, expiryDate, message, false, Instant.now());
    }

    public static DocumentExpiryAlert reconstitute(DocumentExpiryAlertId id, PromoterId promoterId,
                                                    ArtistId artistId, BandMemberId bandMemberId,
                                                    IdentityDocumentType documentType, LocalDate expiryDate,
                                                    String message, boolean read, Instant createdAt) {
        return new DocumentExpiryAlert(id, promoterId, artistId, bandMemberId, documentType,
                expiryDate, message, read, createdAt);
    }

    // ── Comportamiento ─────────────────────────────────────────────────────────

    public void markRead() {
        this.read = true;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public DocumentExpiryAlertId getId()           { return id; }
    public PromoterId            getPromoterId()   { return promoterId; }
    public ArtistId              getArtistId()     { return artistId; }
    public BandMemberId          getBandMemberId() { return bandMemberId; }
    public IdentityDocumentType  getDocumentType() { return documentType; }
    public LocalDate             getExpiryDate()   { return expiryDate; }
    public String                getMessage()      { return message; }
    public boolean               isRead()          { return read; }
    public Instant               getCreatedAt()    { return createdAt; }
}
