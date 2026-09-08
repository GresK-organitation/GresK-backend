package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.RunViabilitySimulationCommand;
import com.gresk.modules.show.application.port.in.RunViabilitySimulationUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.valueobject.CostLineItem;
import com.gresk.modules.show.domain.model.valueobject.FinancialSimulation;
import com.gresk.modules.show.domain.model.valueobject.FinancialViabilityReport;
import com.gresk.modules.show.domain.model.valueobject.RevenueAssumptions;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import com.gresk.shared.domain.valueobject.Percentage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RunViabilitySimulationService implements RunViabilitySimulationUseCase {

    private final ShowRepositoryPort showRepository;

    @Override
    public FinancialViabilityReport execute(RunViabilitySimulationCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());

        var costLineItems = command.costLineItems().stream()
                .map(i -> new CostLineItem(i.category(), i.label(), i.amount(), i.nature()))
                .toList();
        var revenueAssumptions = new RevenueAssumptions(
                command.avgTicketPrice(), Percentage.of(command.expectedSelloutPercent()), command.currency());

        FinancialViabilityReport report = show.runViabilitySimulation(
                new FinancialSimulation(costLineItems, revenueAssumptions));
        showRepository.save(show);
        return report;
    }
}
