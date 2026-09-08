package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.exception.EpkShareLinkExpiredException;
import com.gresk.modules.artist.domain.exception.EpkShareLinkRevokedException;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkShareLinkId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Enlace temporal de descarga pública para una versión concreta de un EpkAsset.
 * Entidad independiente (con repositorio propio) porque su cardinalidad no está
 * acotada (un dossier puede generar varios links a distintos medios) y su ciclo
 * de vida —expirar, revocar, contar descargas— es independiente del histórico
 * de versiones del documento.
 */
public final class EpkShareLink {

    private final EpkShareLinkId id;
    private final EpkAssetId     epkAssetId;
    private final int            versionNumber;
    private final PromoterId     promoterId;
    private final String         token;
    private final Instant        expiresAt;
    private final Integer        maxDownloads;
    private int                  downloadCount;
    private boolean              revoked;
    private final String         createdByUserId;
    private final Instant        createdAt;

    private EpkShareLink(EpkShareLinkId id, EpkAssetId epkAssetId, int versionNumber,
                          PromoterId promoterId, String token, Instant expiresAt,
                          Integer maxDownloads, int downloadCount, boolean revoked,
                          String createdByUserId, Instant createdAt) {
        this.id              = Objects.requireNonNull(id, "EpkShareLinkId is required");
        this.epkAssetId      = Objects.requireNonNull(epkAssetId, "EpkAssetId is required");
        this.versionNumber   = versionNumber;
        this.promoterId      = Objects.requireNonNull(promoterId, "PromoterId is required");
        this.token           = Objects.requireNonNull(token, "token is required");
        this.expiresAt       = Objects.requireNonNull(expiresAt,
                "expiresAt is required: un EpkShareLink siempre debe caducar");
        this.maxDownloads    = maxDownloads;
        this.downloadCount   = downloadCount;
        this.revoked         = revoked;
        this.createdByUserId = createdByUserId;
        this.createdAt       = Objects.requireNonNull(createdAt, "CreatedAt is required");
    }

    // ── Factoría: emisión de un nuevo enlace ──────────────────────────────────

    public static EpkShareLink issue(EpkAssetId epkAssetId, int versionNumber, PromoterId promoterId,
                                      Duration validFor, Integer maxDownloads, String createdByUserId) {
        if (validFor == null || validFor.isNegative() || validFor.isZero()) {
            throw new IllegalArgumentException("validFor must be a positive duration");
        }
        Instant now = Instant.now();
        return new EpkShareLink(EpkShareLinkId.generate(), epkAssetId, versionNumber, promoterId,
                UUID.randomUUID().toString().replace("-", ""), now.plus(validFor),
                maxDownloads, 0, false, createdByUserId, now);
    }

    // ── Factoría: reconstitución desde persistencia ───────────────────────────

    public static EpkShareLink reconstitute(EpkShareLinkId id, EpkAssetId epkAssetId, int versionNumber,
                                             PromoterId promoterId, String token, Instant expiresAt,
                                             Integer maxDownloads, int downloadCount, boolean revoked,
                                             String createdByUserId, Instant createdAt) {
        return new EpkShareLink(id, epkAssetId, versionNumber, promoterId, token, expiresAt,
                maxDownloads, downloadCount, revoked, createdByUserId, createdAt);
    }

    // ── Comportamiento ─────────────────────────────────────────────────────────

    /** Valida el enlace y registra una descarga. Lanza excepción de dominio si no es utilizable. */
    public void registerDownload() {
        if (revoked) {
            throw new EpkShareLinkRevokedException(id.toString());
        }
        if (Instant.now().isAfter(expiresAt)) {
            throw new EpkShareLinkExpiredException(id.toString());
        }
        if (maxDownloads != null && downloadCount >= maxDownloads) {
            throw new EpkShareLinkExpiredException(id.toString());
        }
        downloadCount++;
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isUsable() {
        return !revoked
                && Instant.now().isBefore(expiresAt)
                && (maxDownloads == null || downloadCount < maxDownloads);
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public EpkShareLinkId getId()              { return id; }
    public EpkAssetId     getEpkAssetId()      { return epkAssetId; }
    public int            getVersionNumber()   { return versionNumber; }
    public PromoterId     getPromoterId()      { return promoterId; }
    public String         getToken()           { return token; }
    public Instant        getExpiresAt()       { return expiresAt; }
    public Integer        getMaxDownloads()    { return maxDownloads; }
    public int            getDownloadCount()   { return downloadCount; }
    public boolean        isRevoked()          { return revoked; }
    public String         getCreatedByUserId() { return createdByUserId; }
    public Instant        getCreatedAt()       { return createdAt; }
}
