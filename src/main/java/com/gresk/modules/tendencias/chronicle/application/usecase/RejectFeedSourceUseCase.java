package com.gresk.modules.tendencias.chronicle.application.usecase;

import com.gresk.modules.tendencias.chronicle.domain.exception.FeedSourceNotFoundException;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceId;
import com.gresk.modules.tendencias.chronicle.domain.port.out.FeedSourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RejectFeedSourceUseCase {

    private final FeedSourceRepositoryPort feedSourceRepository;

    public FeedSource execute(FeedSourceId id, UUID adminId) {
        FeedSource feedSource = feedSourceRepository.findById(id)
                .orElseThrow(() -> new FeedSourceNotFoundException("Feed source not found: " + id));
        feedSource.reject(adminId);
        feedSourceRepository.save(feedSource);
        return feedSource;
    }
}
