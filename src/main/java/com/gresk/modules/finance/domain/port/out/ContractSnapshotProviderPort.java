package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.contract.domain.model.Contract;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de solo lectura hacia contract, calcado del patrón de ArtistTourHistoryAdapter:
 * finance no persiste nada de Contract, solo lo lee para snapshotear sus términos al
 * crear un SettlementAgreement.
 */
public interface ContractSnapshotProviderPort {
    Optional<Contract> findById(UUID contractId);
}
