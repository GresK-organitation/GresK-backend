package com.gresk.modules.musicdna.infrastructure.event;

import com.gresk.modules.journal.application.event.JournalEntryCreatedEvent;
import com.gresk.modules.musicdna.application.usecase.CalculateUserMusicDnaUseCase;
import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.review.application.event.ReviewSubmittedEvent;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Dispara el primer cálculo del ADN Musical en cuanto el usuario documenta
 * su primera Review o JournalEntry, o lo refresca si ya tenía 30+ días.
 * Se ejecuta tras el commit de la transacción que creó el contenido, para
 * garantizar que ya es visible en base de datos al leer las señales.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MusicDnaContentCreatedListener {

    private static final int STALE_AFTER_DAYS = 30;

    private final MusicDnaRepository           repository;
    private final CalculateUserMusicDnaUseCase calculateUserMusicDnaUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReviewSubmitted(ReviewSubmittedEvent event) {
        handle(UserId.of(event.userId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJournalEntryCreated(JournalEntryCreatedEvent event) {
        handle(UserId.of(event.userId()));
    }

    private void handle(UserId userId) {
        try {
            Optional<UserMusicDna> existing = repository.findByUserId(userId);
            boolean isFirstContent = existing.isEmpty();
            boolean isStale = existing
                    .map(dna -> dna.getCalculatedAt().isBefore(Instant.now().minus(STALE_AFTER_DAYS, ChronoUnit.DAYS)))
                    .orElse(false);

            if (isFirstContent || isStale) {
                calculateUserMusicDnaUseCase.execute(userId);
            }
        } catch (Exception e) {
            log.error("Async Music DNA recalculation failed for user {}: {}", userId, e.getMessage());
        }
    }
}
