package com.gresk.modules.finance.domain.model.valueobject;

/**
 * Contacto de una factura (emitida o recibida). Deliberadamente distinto de
 * contract.ContractParty: el destinatario/proveedor de una factura no tiene por qué
 * coincidir con ninguna de las dos partes del contrato de actuación.
 */
public record InvoiceParty(
        String name,
        String taxId,
        String address,
        String country,
        String email
) {
    public InvoiceParty {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
    }
}
