package com.gresk.modules.musicdna.infrastructure.adapter;

import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignalsPort;
import com.gresk.modules.musicdna.infrastructure.persistence.JournalSignalsRow;
import com.gresk.modules.musicdna.infrastructure.persistence.MusicDnaSignalsQueryRepository;
import com.gresk.modules.musicdna.infrastructure.persistence.ReviewSignalsRow;
import com.gresk.modules.user.domain.exception.UserNotFoundException;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Compone las queries agregadas de {@link MusicDnaSignalsQueryRepository}
 * con el puerto de lectura ya existente de `user` para ensamblar el DTO
 * {@link MusicDnaSignals} — lo único que ve el dominio de musicdna.
 */
@Component
@RequiredArgsConstructor
public class JpaMusicDnaSignalsAdapter implements MusicDnaSignalsPort {

    private final MusicDnaSignalsQueryRepository queryRepository;
    private final UserRepositoryPort             userRepositoryPort;

    @Override
    public MusicDnaSignals findSignals(UserId userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ReviewSignalsRow  reviewSignals  = queryRepository.findReviewSignals(userId.value());
        JournalSignalsRow journalSignals = queryRepository.findJournalSignals(userId.value());
        int       distinctGenresMin2   = queryRepository.countDistinctGenresMin2(userId.value());
        LocalDate oldestDocumentedDate = queryRepository.findOldestDocumentedDate(userId.value());

        return new MusicDnaSignals(
                reviewSignals.getReviewCount(),
                journalSignals.getJournalCount(),
                reviewSignals.getWrittenCount(),
                journalSignals.getWrittenCount(),
                reviewSignals.getTotalLikes(),
                journalSignals.getTotalCustomCriteria(),
                distinctGenresMin2,
                reviewSignals.getLocalMatches(),
                journalSignals.getLocalMatches(),
                oldestDocumentedDate,
                user.getCreatedAt()
        );
    }

    @Override
    public List<UserId> findUserIdsWithActivitySince(Instant since) {
        return queryRepository.findUserIdsWithActivitySince(since).stream().map(UserId::of).toList();
    }

    @Override
    public List<String> findTopGenres(UserId userId, int limit) {
        return queryRepository.findTopGenres(userId.value(), limit);
    }
}
