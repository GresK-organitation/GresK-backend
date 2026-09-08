package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.exception.EpkAssetArchivedException;
import com.gresk.modules.artist.domain.exception.EpkAssetVersionNotFoundException;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetType;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetVersion;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.AssetId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Documento versionado del repositorio EPK de un artista (dossier, logo,
 * foto de prensa, rider). Aggregate independiente de Artist —referencia
 * ArtistId por valor, igual que TechnicalRider— porque su ciclo de vida
 * (nuevas versiones, archivado) no debe forzar cargar/guardar el aggregate
 * Artist completo.
 */
public final class EpkAsset {

    private final EpkAssetId id;
    private final ArtistId artistId;
    private final PromoterId promoterId;
    private final EpkAssetType type;
    private String label;
    private final List<EpkAssetVersion> versions;
    private boolean archived;
    private final Instant createdAt;
    private Instant updatedAt;

    private EpkAsset(EpkAssetId id, ArtistId artistId, PromoterId promoterId, EpkAssetType type,
                      String label, List<EpkAssetVersion> versions, boolean archived,
                      Instant createdAt, Instant updatedAt) {
        this.id         = Objects.requireNonNull(id, "EpkAssetId is required");
        this.artistId   = Objects.requireNonNull(artistId, "ArtistId is required");
        this.promoterId = Objects.requireNonNull(promoterId, "PromoterId is required");
        this.type       = Objects.requireNonNull(type, "EpkAssetType is required");
        this.label      = Objects.requireNonNull(label, "Label is required");
        this.versions   = versions != null ? new ArrayList<>(versions) : new ArrayList<>();
        if (this.versions.isEmpty()) {
            throw new IllegalArgumentException("EpkAsset must have at least one version");
        }
        this.archived  = archived;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt is required");
        this.updatedAt = updatedAt != null ? updatedAt : createdAt;
    }

    // ── Factoría: creación con la primera versión ─────────────────────────────

    public static EpkAsset create(ArtistId artistId, PromoterId promoterId, EpkAssetType type,
                                   String label, AssetId storedFile, String fileName,
                                   String mimeType, long fileSizeBytes, String uploadedByUserId) {
        Instant now = Instant.now();
        EpkAssetVersion firstVersion = EpkAssetVersion.first(
                storedFile, fileName, mimeType, fileSizeBytes, uploadedByUserId, now);
        return new EpkAsset(EpkAssetId.generate(), artistId, promoterId, type, label,
                List.of(firstVersion), false, now, now);
    }

    // ── Factoría: reconstitución desde persistencia ───────────────────────────

    public static EpkAsset reconstitute(EpkAssetId id, ArtistId artistId, PromoterId promoterId,
                                         EpkAssetType type, String label, List<EpkAssetVersion> versions,
                                         boolean archived, Instant createdAt, Instant updatedAt) {
        return new EpkAsset(id, artistId, promoterId, type, label, versions, archived, createdAt, updatedAt);
    }

    // ── Comportamiento ─────────────────────────────────────────────────────────

    public EpkAssetVersion addVersion(AssetId storedFile, String fileName, String mimeType,
                                       long fileSizeBytes, String uploadedByUserId) {
        if (archived) {
            throw new EpkAssetArchivedException(id.toString());
        }
        int nextVersionNumber = currentVersion().versionNumber() + 1;
        EpkAssetVersion version = EpkAssetVersion.of(nextVersionNumber, storedFile, fileName,
                mimeType, fileSizeBytes, uploadedByUserId, Instant.now());
        versions.add(version);
        updatedAt = Instant.now();
        return version;
    }

    public EpkAssetVersion currentVersion() {
        return versions.get(versions.size() - 1);
    }

    public EpkAssetVersion findVersion(int versionNumber) {
        return versions.stream()
                .filter(v -> v.versionNumber() == versionNumber)
                .findFirst()
                .orElseThrow(() -> new EpkAssetVersionNotFoundException(id.toString(), versionNumber));
    }

    public void rename(String newLabel) {
        this.label = Objects.requireNonNull(newLabel, "label is required");
        this.updatedAt = Instant.now();
    }

    public void archive() {
        this.archived = true;
        this.updatedAt = Instant.now();
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public EpkAssetId            getId()          { return id; }
    public ArtistId              getArtistId()    { return artistId; }
    public PromoterId            getPromoterId()  { return promoterId; }
    public EpkAssetType          getType()        { return type; }
    public String                getLabel()       { return label; }
    public List<EpkAssetVersion> getVersions()    { return Collections.unmodifiableList(versions); }
    public boolean                isArchived()    { return archived; }
    public Instant                getCreatedAt()  { return createdAt; }
    public Instant                getUpdatedAt()  { return updatedAt; }
}
