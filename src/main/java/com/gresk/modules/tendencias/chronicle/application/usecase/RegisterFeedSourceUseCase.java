package com.gresk.modules.tendencias.chronicle.application.usecase;

import com.gresk.modules.tendencias.chronicle.application.command.RegisterFeedSourceCommand;
import com.gresk.modules.tendencias.chronicle.domain.exception.DuplicateFeedSourceException;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.port.out.FeedSourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterFeedSourceUseCase {

    private final FeedSourceRepositoryPort feedSourceRepository;

    public FeedSource execute(RegisterFeedSourceCommand command) {
        if (feedSourceRepository.existsByFeedUrl(command.feedUrl())) {
            throw new DuplicateFeedSourceException("Feed source already registered: " + command.feedUrl());
        }
        FeedSource feedSource = FeedSource.register(command.name(), command.feedUrl(), command.sourceUrl());
        feedSourceRepository.save(feedSource);
        return feedSource;
    }
}
