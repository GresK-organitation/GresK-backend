package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.RunViabilitySimulationCommand;
import com.gresk.modules.show.domain.model.valueobject.FinancialViabilityReport;

/** El simulador de viabilidad (P&amp;L Borrador): recalcula el break-even cada vez que se invoca. */
public interface RunViabilitySimulationUseCase {
    FinancialViabilityReport execute(RunViabilitySimulationCommand command);
}
