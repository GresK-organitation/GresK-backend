package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.CancelShowCommand;
import com.gresk.modules.show.application.port.in.CancelShowUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.model.valueobject.LogEntryType;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelShowService implements CancelShowUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;

    @Override
    public Show execute(CancelShowCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());
        ShowStatus from = show.getStatus();

        show.cancel(command.reason());
        Show saved = showRepository.save(show);

        showLogRepository.save(ShowLogEntry.statusChange(saved.getId(), command.promoterId(), from, saved.getStatus()));
        if (command.reason() != null && !command.reason().isBlank()) {
            showLogRepository.save(ShowLogEntry.record(saved.getId(), LogEntryType.DECISION, command.promoterId(),
                    "Motivo de cancelación: " + command.reason(), null, List.of()));
        }
        return saved;
    }
}
