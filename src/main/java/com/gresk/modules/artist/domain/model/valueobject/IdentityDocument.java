package com.gresk.modules.artist.domain.model.valueobject;

import com.gresk.modules.artist.domain.exception.InvalidIdentityDocumentException;

import java.time.LocalDate;

/**
 * Documento de identidad de un miembro de banda. Se reemplaza entero al
 * renovarse (no lleva versión propia, a diferencia de EpkAssetVersion).
 * expiryDate es nullable: algunos documentos no caducan o el dato aún no se
 * conoce; si es null, ese documento simplemente no genera alertas de caducidad.
 */
public record IdentityDocument(
        IdentityDocumentType type,
        String documentNumber,
        String issuingCountry,
        LocalDate expiryDate
) {
    public IdentityDocument {
        if (type == null) {
            throw new InvalidIdentityDocumentException("Document type is required");
        }
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new InvalidIdentityDocumentException("Document number is required");
        }
        documentNumber = documentNumber.trim();
        if (issuingCountry == null || issuingCountry.isBlank()) {
            throw new InvalidIdentityDocumentException("Issuing country is required");
        }
        issuingCountry = issuingCountry.trim();
    }

    public boolean isExpiringBefore(LocalDate cutoff) {
        return expiryDate != null && !expiryDate.isAfter(cutoff);
    }

    public static IdentityDocument of(IdentityDocumentType type, String documentNumber,
                                       String issuingCountry, LocalDate expiryDate) {
        return new IdentityDocument(type, documentNumber, issuingCountry, expiryDate);
    }
}
