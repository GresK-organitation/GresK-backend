package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface ContractRepositoryPort {
    Contract                save(Contract contract);
    Optional<Contract>      findById(ContractId id);
    Optional<Contract>      findByShareToken(String token);
    List<Contract>          findByPromoterId(PromoterId promoterId);
    List<Contract>          findByPromoterIdAndStatus(PromoterId promoterId, ContractStatus status);
    List<Contract>          findByPromoterIdAndType(PromoterId promoterId, ContractType type);
    List<Contract>          findByLinkedEventId(java.util.UUID eventId);
    ContractStats           statsForPromoter(PromoterId promoterId);
    int                     countByPromoterIdForYear(PromoterId promoterId, int year);
}
