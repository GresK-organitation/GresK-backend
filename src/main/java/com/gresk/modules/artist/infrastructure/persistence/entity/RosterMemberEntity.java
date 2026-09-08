package com.gresk.modules.artist.infrastructure.persistence.entity;

import com.gresk.modules.artist.domain.model.valueobject.ContactRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "roster_member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RosterMemberEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactRole role;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "billing_legal_name", length = 255)
    private String billingLegalName;

    @Column(name = "billing_tax_id", length = 255)
    private String billingTaxId;

    @Column(name = "billing_address", length = 255)
    private String billingAddress;

    @Column(name = "billing_iban", length = 255)
    private String billingIban;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryContact;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateContactInfo(String name, ContactRole role, String phone, String email) {
        this.name  = name;
        this.role  = role;
        this.phone = phone;
        this.email = email;
    }

    public void updateBilling(String legalName, String taxId, String address, String iban) {
        this.billingLegalName = legalName;
        this.billingTaxId     = taxId;
        this.billingAddress   = address;
        this.billingIban      = iban;
    }

    public void updateFlags(boolean primaryContact, boolean active) {
        this.primaryContact = primaryContact;
        this.active         = active;
    }
}
