package com.gresk.modules.tendencias.chronicle.application.usecase;

import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.port.out.FeedSourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListFeedSourcesUseCase {

    private final FeedSourceRepositoryPort feedSourceRepository;

    public List<FeedSource> execute() {
        return feedSourceRepository.findAll();
    }
}
