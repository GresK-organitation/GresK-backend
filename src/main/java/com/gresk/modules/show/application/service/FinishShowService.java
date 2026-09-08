package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.port.in.FinishShowUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FinishShowService implements FinishShowUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;

    @Override
    public Show execute(String showId, String promoterId) {
        Show show = ShowLookup.findOwned(showRepository, showId, promoterId);
        ShowStatus from = show.getStatus();
        show.finish();
        Show saved = showRepository.save(show);
        showLogRepository.save(ShowLogEntry.statusChange(saved.getId(), promoterId, from, saved.getStatus()));
        return saved;
    }
}
