package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.PlaceHoldCommand;
import com.gresk.modules.show.application.port.in.PlaceHoldUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.model.valueobject.HoldWindow;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceHoldService implements PlaceHoldUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;

    @Override
    public Show execute(PlaceHoldCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());
        ShowStatus from = show.getStatus();

        Instant expiresAt = Instant.now().plus(command.holdDurationHours(), ChronoUnit.HOURS);
        show.placeHold(HoldWindow.startingNow(expiresAt));
        Show saved = showRepository.save(show);

        showLogRepository.save(ShowLogEntry.statusChange(saved.getId(), command.promoterId(), from, saved.getStatus()));
        return saved;
    }
}
