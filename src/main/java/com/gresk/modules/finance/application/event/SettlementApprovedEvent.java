package com.gresk.modules.finance.application.event;

import com.gresk.modules.finance.domain.model.SettlementId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.context.ApplicationEvent;

public class SettlementApprovedEvent extends ApplicationEvent {

    private final SettlementId settlementId;
    private final PromoterId   promoterId;
    private final Money        artistPayableAmount;

    public SettlementApprovedEvent(Object source, SettlementId settlementId, PromoterId promoterId,
                                    Money artistPayableAmount) {
        super(source);
        this.settlementId        = settlementId;
        this.promoterId          = promoterId;
        this.artistPayableAmount = artistPayableAmount;
    }

    public SettlementId getSettlementId()        { return settlementId; }
    public PromoterId   getPromoterId()          { return promoterId; }
    public Money        getArtistPayableAmount() { return artistPayableAmount; }
}
