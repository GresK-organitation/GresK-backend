package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.AuditTrailEntry;
import com.gresk.modules.contract.domain.model.ContractId;

import java.util.List;

/** Sin update/delete a propósito: así se impone que el audit trail sea append-only. */
public interface AuditTrailRepositoryPort {
    void append(AuditTrailEntry entry);
    List<AuditTrailEntry> findByContractId(ContractId contractId);
}
