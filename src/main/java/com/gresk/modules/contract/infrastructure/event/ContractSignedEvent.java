package com.gresk.modules.contract.infrastructure.event;

import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.UUID;

public class ContractSignedEvent extends ApplicationEvent {

    private final ContractId   contractId;
    private final PromoterId   promoterId;
    private final ContractType contractType;
    private final UUID         linkedEventId;
    private final String       referenceNumber;
    private final String       partyBName;
    private final BigDecimal   feeAmount;

    public ContractSignedEvent(Object source, ContractId contractId, PromoterId promoterId,
                                ContractType contractType, UUID linkedEventId,
                                String referenceNumber, String partyBName, BigDecimal feeAmount) {
        super(source);
        this.contractId      = contractId;
        this.promoterId      = promoterId;
        this.contractType    = contractType;
        this.linkedEventId   = linkedEventId;
        this.referenceNumber = referenceNumber;
        this.partyBName      = partyBName;
        this.feeAmount       = feeAmount;
    }

    public ContractId   getContractId()      { return contractId; }
    public PromoterId   getPromoterId()      { return promoterId; }
    public ContractType getContractType()    { return contractType; }
    public UUID         getLinkedEventId()   { return linkedEventId; }
    public String       getReferenceNumber() { return referenceNumber; }
    public String       getPartyBName()      { return partyBName; }
    public BigDecimal   getFeeAmount()       { return feeAmount; }
}
