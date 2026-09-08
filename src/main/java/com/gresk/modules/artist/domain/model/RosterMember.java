package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BillingDetails;
import com.gresk.modules.artist.domain.model.valueobject.ContactRole;
import com.gresk.modules.artist.domain.model.valueobject.RosterMemberId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;

import java.time.Instant;
import java.util.Objects;

/**
 * Miembro del equipo de representación de un artista (booking agent, manager,
 * tour manager, PR...). Entidad propia con repositorio propio —no un VO
 * embebido en Artist— porque su cardinalidad no está acotada y lleva datos
 * de facturación con ciclo de vida independiente del perfil del artista.
 */
public final class RosterMember {

    private final RosterMemberId id;
    private final ArtistId       artistId;
    private final PromoterId     promoterId;
    private Name                 name;
    private ContactRole          role;
    private String               phone;
    private Email                email;
    private BillingDetails       billingDetails;
    private boolean              primary;
    private boolean              active;
    private final Instant        createdAt;
    private Instant              updatedAt;

    private RosterMember(RosterMemberId id, ArtistId artistId, PromoterId promoterId, Name name,
                          ContactRole role, String phone, Email email, BillingDetails billingDetails,
                          boolean primary, boolean active, Instant createdAt, Instant updatedAt) {
        this.id             = Objects.requireNonNull(id, "RosterMemberId is required");
        this.artistId       = Objects.requireNonNull(artistId, "ArtistId is required");
        this.promoterId     = Objects.requireNonNull(promoterId, "PromoterId is required");
        this.name           = Objects.requireNonNull(name, "Name is required");
        this.role           = Objects.requireNonNull(role, "ContactRole is required");
        this.phone          = phone;
        this.email          = email;
        this.billingDetails = billingDetails != null ? billingDetails : BillingDetails.empty();
        this.primary        = primary;
        this.active         = active;
        this.createdAt      = Objects.requireNonNull(createdAt, "CreatedAt is required");
        this.updatedAt      = updatedAt != null ? updatedAt : createdAt;
    }

    // ── Factories ──────────────────────────────────────────────────────────────

    public static RosterMember create(ArtistId artistId, PromoterId promoterId, Name name,
                                       ContactRole role, String phone, Email email,
                                       BillingDetails billingDetails, boolean primary) {
        Instant now = Instant.now();
        return new RosterMember(RosterMemberId.generate(), artistId, promoterId, name, role,
                phone, email, billingDetails, primary, true, now, now);
    }

    public static RosterMember reconstitute(RosterMemberId id, ArtistId artistId, PromoterId promoterId,
                                             Name name, ContactRole role, String phone, Email email,
                                             BillingDetails billingDetails, boolean primary, boolean active,
                                             Instant createdAt, Instant updatedAt) {
        return new RosterMember(id, artistId, promoterId, name, role, phone, email,
                billingDetails, primary, active, createdAt, updatedAt);
    }

    // ── Comportamiento ─────────────────────────────────────────────────────────

    public void updateContactInfo(Name name, ContactRole role, String phone, Email email) {
        this.name  = Objects.requireNonNull(name, "Name is required");
        this.role  = Objects.requireNonNull(role, "ContactRole is required");
        this.phone = phone;
        this.email = email;
        this.updatedAt = Instant.now();
    }

    public void updateBillingDetails(BillingDetails billingDetails) {
        this.billingDetails = billingDetails != null ? billingDetails : BillingDetails.empty();
        this.updatedAt = Instant.now();
    }

    public void changeRole(ContactRole role) {
        this.role = Objects.requireNonNull(role, "ContactRole is required");
        this.updatedAt = Instant.now();
    }

    public void markAsPrimary() {
        this.primary = true;
        this.updatedAt = Instant.now();
    }

    public void unmarkAsPrimary() {
        this.primary = false;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void reactivate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public RosterMemberId  getId()             { return id; }
    public ArtistId        getArtistId()       { return artistId; }
    public PromoterId      getPromoterId()     { return promoterId; }
    public Name            getName()           { return name; }
    public ContactRole     getRole()           { return role; }
    public String          getPhone()          { return phone; }
    public Email            getEmail()          { return email; }
    public BillingDetails  getBillingDetails() { return billingDetails; }
    public boolean         isPrimary()         { return primary; }
    public boolean         isActive()          { return active; }
    public Instant         getCreatedAt()      { return createdAt; }
    public Instant         getUpdatedAt()      { return updatedAt; }
}
