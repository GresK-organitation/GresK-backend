package com.gresk.modules.artist.infrastructure.persistence.entity;

import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocumentEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", length = 20, nullable = false)
    private IdentityDocumentType type;

    @Column(name = "document_number", length = 100, nullable = false)
    private String documentNumber;

    @Column(name = "issuing_country", length = 100, nullable = false)
    private String issuingCountry;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}
