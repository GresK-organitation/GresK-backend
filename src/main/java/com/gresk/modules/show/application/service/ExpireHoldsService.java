package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.port.in.ExpireHoldsUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpireHoldsService implements ExpireHoldsUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;

    @Override
    public int execute() {
        List<Show> expirable = showRepository.findExpirableHolds(Instant.now());
        for (Show show : expirable) {
            ShowStatus from = show.getStatus();
            show.expireHold();
            showRepository.save(show);
            showLogRepository.save(ShowLogEntry.statusChange(show.getId(), "system", from, show.getStatus()));
        }
        return expirable.size();
    }
}
