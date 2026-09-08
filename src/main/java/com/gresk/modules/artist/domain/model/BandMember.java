package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Name;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Miembro individual de una banda/artista (persona física). Concepto nuevo:
 * Artist modelaba hasta ahora el artista/banda como unidad, sin miembros
 * individuales. Entidad propia con repositorio propio y aislada del
 * aggregate Artist porque contiene datos personales sensibles (documentos
 * de identidad) con necesidades de retención/consentimiento distintas del
 * resto del perfil del artista.
 */
public final class BandMember {

    private final BandMemberId id;
    private final ArtistId     artistId;
    private final PromoterId   promoterId;
    private Name                name;
    private String              roleInBand;
    private final List<IdentityDocument> documents;
    private boolean             active;
    private final Instant       createdAt;
    private Instant             updatedAt;

    private BandMember(BandMemberId id, ArtistId artistId, PromoterId promoterId, Name name,
                        String roleInBand, List<IdentityDocument> documents, boolean active,
                        Instant createdAt, Instant updatedAt) {
        this.id         = Objects.requireNonNull(id, "BandMemberId is required");
        this.artistId   = Objects.requireNonNull(artistId, "ArtistId is required");
        this.promoterId = Objects.requireNonNull(promoterId, "PromoterId is required");
        this.name       = Objects.requireNonNull(name, "Name is required");
        this.roleInBand = roleInBand;
        this.documents  = documents != null ? new ArrayList<>(documents) : new ArrayList<>();
        this.active     = active;
        this.createdAt  = Objects.requireNonNull(createdAt, "CreatedAt is required");
        this.updatedAt  = updatedAt != null ? updatedAt : createdAt;
    }

    // ── Factories ──────────────────────────────────────────────────────────────

    public static BandMember create(ArtistId artistId, PromoterId promoterId, Name name, String roleInBand) {
        Instant now = Instant.now();
        return new BandMember(BandMemberId.generate(), artistId, promoterId, name, roleInBand,
                new ArrayList<>(), true, now, now);
    }

    public static BandMember reconstitute(BandMemberId id, ArtistId artistId, PromoterId promoterId,
                                           Name name, String roleInBand, List<IdentityDocument> documents,
                                           boolean active, Instant createdAt, Instant updatedAt) {
        return new BandMember(id, artistId, promoterId, name, roleInBand, documents, active, createdAt, updatedAt);
    }

    // ── Comportamiento ─────────────────────────────────────────────────────────

    /** Añade el documento o, si ya existe uno del mismo tipo, lo reemplaza (renovación). */
    public void addOrRenewDocument(IdentityDocument document) {
        Objects.requireNonNull(document, "document is required");
        documents.removeIf(d -> d.type() == document.type());
        documents.add(document);
        updatedAt = Instant.now();
    }

    public void removeDocument(IdentityDocumentType type) {
        documents.removeIf(d -> d.type() == type);
        updatedAt = Instant.now();
    }

    public List<IdentityDocument> documentsExpiringBefore(LocalDate cutoff) {
        return documents.stream()
                .filter(d -> d.isExpiringBefore(cutoff))
                .toList();
    }

    public void updateRoleInBand(String roleInBand) {
        this.roleInBand = roleInBand;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(Name name, String roleInBand) {
        this.name = Objects.requireNonNull(name, "Name is required");
        this.roleInBand = roleInBand;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public BandMemberId           getId()          { return id; }
    public ArtistId               getArtistId()    { return artistId; }
    public PromoterId             getPromoterId()  { return promoterId; }
    public Name                   getName()        { return name; }
    public String                 getRoleInBand()  { return roleInBand; }
    public List<IdentityDocument> getDocuments()   { return Collections.unmodifiableList(documents); }
    public boolean                isActive()       { return active; }
    public Instant                getCreatedAt()   { return createdAt; }
    public Instant                getUpdatedAt()   { return updatedAt; }
}
