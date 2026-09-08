package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.CalculateWithholdingCommand;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingApplication;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;
import com.gresk.modules.finance.domain.service.WithholdingCalculator;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Service;

/** Utilidad de previsualización, sin persistencia — permite mostrar la retención antes de registrar el pago. */
@Service
public class CalculateWithholdingUseCase {

    public WithholdingApplication execute(CalculateWithholdingCommand cmd) {
        Money taxBase = new Money(cmd.taxBaseAmount(), cmd.currency() != null ? cmd.currency() : "EUR");
        WithholdingKind kind = cmd.withholdingKind() != null ? cmd.withholdingKind() : WithholdingKind.NONE;
        return WithholdingCalculator.apply(taxBase, kind, cmd.ratePercentageOverride(), cmd.exemptionReason());
    }
}
