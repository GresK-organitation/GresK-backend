package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.port.in.GetShowTimelineUseCase;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetShowTimelineService implements GetShowTimelineUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;

    @Override
    public List<ShowLogEntry> execute(String showId, String promoterId) {
        var show = ShowLookup.findOwned(showRepository, showId, promoterId);
        return showLogRepository.findByShowId(show.getId());
    }
}
