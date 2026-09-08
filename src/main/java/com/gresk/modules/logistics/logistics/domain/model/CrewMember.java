package com.gresk.modules.logistics.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.logistics.domain.exception.InvalidCrewMemberException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Name;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Personal de gira contratado directamente por la promotora y sin vínculo a ningún
 * Artist (tour manager, técnicos, conductores...). Reutiliza IdentityDocument del
 * módulo artist (mismo patrón que artist.BandMember) para pasaporte/visado; los
 * músicos de la banda se referencian por su propio BandMemberId en vez de duplicarse.
 */
public final class CrewMember {

    private final CrewMemberId id;
    private final PromoterId promoterId;
    private final Instant createdAt;

    private Name name;
    private String defaultRole;
    private String contactPhone;
    private String contactEmail;
    private final List<IdentityDocument> documents;
    private boolean active;
    private Instant updatedAt;

    private CrewMember(CrewMemberId id, PromoterId promoterId, Instant createdAt, Name name, String defaultRole,
                        String contactPhone, String contactEmail, List<IdentityDocument> documents, boolean active,
                        Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "CrewMemberId is required");
        this.promoterId = Objects.requireNonNull(promoterId, "PromoterId is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.name = Objects.requireNonNull(name, "Name is required");
        requireRole(defaultRole);
        this.defaultRole = defaultRole;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.documents = documents != null ? new ArrayList<>(documents) : new ArrayList<>();
        this.active = active;
        this.updatedAt = updatedAt != null ? updatedAt : createdAt;
    }

    public static CrewMember create(PromoterId promoterId, Name name, String defaultRole, String contactPhone,
                                     String contactEmail) {
        Instant now = Instant.now();
        return new CrewMember(CrewMemberId.generate(), promoterId, now, name, defaultRole, contactPhone,
                contactEmail, new ArrayList<>(), true, now);
    }

    public static CrewMember reconstitute(CrewMemberId id, PromoterId promoterId, Instant createdAt, Name name,
                                           String defaultRole, String contactPhone, String contactEmail,
                                           List<IdentityDocument> documents, boolean active, Instant updatedAt) {
        return new CrewMember(id, promoterId, createdAt, name, defaultRole, contactPhone, contactEmail, documents,
                active, updatedAt);
    }

    public void updateProfile(Name name, String defaultRole, String contactPhone, String contactEmail) {
        requireRole(defaultRole);
        this.name = Objects.requireNonNull(name, "Name is required");
        this.defaultRole = defaultRole;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        touch();
    }

    /** Añade el documento o, si ya existe uno del mismo tipo, lo reemplaza (renovación). */
    public void addOrRenewDocument(IdentityDocument document) {
        Objects.requireNonNull(document, "document is required");
        documents.removeIf(d -> d.type() == document.type());
        documents.add(document);
        touch();
    }

    public void removeDocument(IdentityDocumentType type) {
        documents.removeIf(d -> d.type() == type);
        touch();
    }

    public void deactivate() {
        this.active = false;
        touch();
    }

    private static void requireRole(String defaultRole) {
        if (defaultRole == null || defaultRole.isBlank()) {
            throw new InvalidCrewMemberException("defaultRole must not be blank");
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public CrewMemberId getId() { return id; }
    public PromoterId getPromoterId() { return promoterId; }
    public Instant getCreatedAt() { return createdAt; }
    public Name getName() { return name; }
    public String getDefaultRole() { return defaultRole; }
    public String getContactPhone() { return contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public List<IdentityDocument> getDocuments() { return List.copyOf(documents); }
    public boolean isActive() { return active; }
    public Instant getUpdatedAt() { return updatedAt; }
}
