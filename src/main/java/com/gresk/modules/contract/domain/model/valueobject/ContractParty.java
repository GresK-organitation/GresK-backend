package com.gresk.modules.contract.domain.model.valueobject;

public record ContractParty(
        String  name,
        String  taxId,
        String  address,
        String  signatoryName,
        String  signatoryRole,
        String  email,
        String  country,       // ISO 3166-1 alpha-2, nullable
        boolean taxResident    // false => puede aplicar retención IRNR
) {
    public ContractParty(String name, String taxId, String address,
                          String signatoryName, String signatoryRole, String email) {
        this(name, taxId, address, signatoryName, signatoryRole, email, null, true);
    }
}
