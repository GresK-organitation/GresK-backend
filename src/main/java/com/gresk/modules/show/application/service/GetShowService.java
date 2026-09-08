package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.port.in.GetShowUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetShowService implements GetShowUseCase {

    private final ShowRepositoryPort showRepository;

    @Override
    public Show execute(String showId, String promoterId) {
        return ShowLookup.findOwned(showRepository, showId, promoterId);
    }
}
