package com.gresk.modules.musicdna.infrastructure.event;

import com.gresk.modules.journal.application.event.JournalEntryCreatedEvent;
import com.gresk.modules.musicdna.application.usecase.CalculateUserMusicDnaUseCase;
import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.review.application.event.ReviewSubmittedEvent;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicDnaContentCreatedListenerTest {

    @Mock private MusicDnaRepository           repository;
    @Mock private CalculateUserMusicDnaUseCase calculateUserMusicDnaUseCase;

    private MusicDnaContentCreatedListener listener;
    private final UUID userIdValue = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        listener = new MusicDnaContentCreatedListener(repository, calculateUserMusicDnaUseCase);
    }

    @Test
    void calculaSiEsElPrimerContenidoDelUsuario() {
        when(repository.findByUserId(UserId.of(userIdValue))).thenReturn(Optional.empty());

        listener.onReviewSubmitted(new ReviewSubmittedEvent(userIdValue));

        verify(calculateUserMusicDnaUseCase).execute(UserId.of(userIdValue));
    }

    @Test
    void noRecalculaSiYaExisteYNoEstaDesactualizado() {
        UserMusicDna dna = dnaCalculatedAt(Instant.now().minus(1, ChronoUnit.DAYS));
        when(repository.findByUserId(UserId.of(userIdValue))).thenReturn(Optional.of(dna));

        listener.onJournalEntryCreated(new JournalEntryCreatedEvent(userIdValue));

        verify(calculateUserMusicDnaUseCase, never()).execute(any());
    }

    @Test
    void recalculaSiElAdnExistenteTiene30OMasDias() {
        UserMusicDna dna = dnaCalculatedAt(Instant.now().minus(31, ChronoUnit.DAYS));
        when(repository.findByUserId(UserId.of(userIdValue))).thenReturn(Optional.of(dna));

        listener.onReviewSubmitted(new ReviewSubmittedEvent(userIdValue));

        verify(calculateUserMusicDnaUseCase).execute(UserId.of(userIdValue));
    }

    private UserMusicDna dnaCalculatedAt(Instant calculatedAt) {
        UserId userId = UserId.of(userIdValue);
        MusicDnaSignals signals = new MusicDnaSignals(3, 1, 2, 1, 3, 3, 2, 2, 0,
                LocalDate.now().minusYears(1), Instant.now().minusSeconds(3600));
        UserMusicDna fresh = UserMusicDna.calculate(userId, signals);
        return UserMusicDna.reconstitute(fresh.getId(), userId, fresh.getIntensidad(), fresh.getDiversidad(),
                fresh.getCriticidad(), fresh.getLocalismo(), fresh.getAntiguedad(), fresh.getAutenticidad(),
                fresh.getSummaryPhrase(), calculatedAt);
    }
}
