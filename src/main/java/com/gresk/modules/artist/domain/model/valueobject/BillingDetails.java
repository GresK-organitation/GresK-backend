package com.gresk.modules.artist.domain.model.valueobject;

import com.gresk.modules.artist.domain.exception.InvalidBillingDetailsException;

/**
 * Datos de facturación de un miembro del Contact Roster. MVP: texto libre
 * (no se reutiliza shared.Address porque exige calle no vacía siempre y aquí
 * la dirección de facturación es opcional). Se estructurará si en el futuro
 * se conecta a un módulo de facturación real.
 */
public record BillingDetails(String legalName, String taxId, String billingAddress, String iban) {

    private static final int MAX_LENGTH = 255;

    public BillingDetails {
        legalName      = sanitize(legalName, "legalName");
        taxId          = sanitize(taxId, "taxId");
        billingAddress = sanitize(billingAddress, "billingAddress");
        iban           = sanitize(iban, "iban");
    }

    public boolean isEmpty() {
        return legalName.isEmpty() && taxId.isEmpty() && billingAddress.isEmpty() && iban.isEmpty();
    }

    public static BillingDetails of(String legalName, String taxId, String billingAddress, String iban) {
        return new BillingDetails(legalName, taxId, billingAddress, iban);
    }

    public static BillingDetails empty() {
        return new BillingDetails("", "", "", "");
    }

    public static BillingDetails reconstitute(String legalName, String taxId, String billingAddress, String iban) {
        return new BillingDetails(legalName, taxId, billingAddress, iban);
    }

    private static String sanitize(String value, String field) {
        if (value == null) return "";
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new InvalidBillingDetailsException(
                    String.format("%s cannot exceed %d characters", field, MAX_LENGTH));
        }
        return value;
    }
}
