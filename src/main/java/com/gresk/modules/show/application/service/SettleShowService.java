package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.SettleShowCommand;
import com.gresk.modules.show.application.port.in.SettleShowUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.model.valueobject.SettlementSummary;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SettleShowService implements SettleShowUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;

    @Override
    public Show execute(SettleShowCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());
        ShowStatus from = show.getStatus();

        show.settle(SettlementSummary.of(command.actualAttendance(), command.actualRevenue(), command.actualCosts()));
        Show saved = showRepository.save(show);

        showLogRepository.save(ShowLogEntry.statusChange(saved.getId(), command.promoterId(), from, saved.getStatus()));
        return saved;
    }
}
