package com.gresk.modules.musicdna.application.usecase;

import com.gresk.modules.musicdna.application.port.out.MusicDnaAsyncRecalculationPort;
import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMyMusicDnaUseCaseTest {

    @Mock private MusicDnaRepository             repository;
    @Mock private MusicDnaAsyncRecalculationPort asyncRecalculationPort;

    private GetMyMusicDnaUseCase useCase;
    private final UserId userId = UserId.of(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        useCase = new GetMyMusicDnaUseCase(repository, asyncRecalculationPort);
    }

    @Test
    void noDisparaRecalculoSiNoExisteAdnTodavia() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<UserMusicDna> result = useCase.execute(userId);

        assertTrue(result.isEmpty());
        verify(asyncRecalculationPort, never()).recalculateAsync(any());
    }

    @Test
    void noDisparaRecalculoSiElAdnEsReciente() {
        UserMusicDna dna = dnaCalculatedAt(Instant.now().minus(1, ChronoUnit.DAYS));
        when(repository.findByUserId(userId)).thenReturn(Optional.of(dna));

        useCase.execute(userId);

        verify(asyncRecalculationPort, never()).recalculateAsync(any());
    }

    @Test
    void disparaRecalculoAsincronoSiElAdnTiene30OMasDias() {
        UserMusicDna dna = dnaCalculatedAt(Instant.now().minus(31, ChronoUnit.DAYS));
        when(repository.findByUserId(userId)).thenReturn(Optional.of(dna));

        Optional<UserMusicDna> result = useCase.execute(userId);

        assertTrue(result.isPresent());
        verify(asyncRecalculationPort).recalculateAsync(userId);
    }

    private UserMusicDna dnaCalculatedAt(Instant calculatedAt) {
        MusicDnaSignals signals = new MusicDnaSignals(3, 1, 2, 1, 3, 3, 2, 2, 0,
                LocalDate.now().minusYears(1), Instant.now().minusSeconds(3600));
        UserMusicDna fresh = UserMusicDna.calculate(userId, signals);
        return UserMusicDna.reconstitute(fresh.getId(), userId, fresh.getIntensidad(), fresh.getDiversidad(),
                fresh.getCriticidad(), fresh.getLocalismo(), fresh.getAntiguedad(), fresh.getAutenticidad(),
                fresh.getSummaryPhrase(), calculatedAt);
    }
}
