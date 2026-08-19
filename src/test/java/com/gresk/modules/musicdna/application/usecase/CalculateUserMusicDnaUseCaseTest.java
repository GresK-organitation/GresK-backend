package com.gresk.modules.musicdna.application.usecase;

import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignalsPort;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateUserMusicDnaUseCaseTest {

    @Mock private MusicDnaSignalsPort signalsPort;
    @Mock private MusicDnaRepository  repository;

    private CalculateUserMusicDnaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CalculateUserMusicDnaUseCase(signalsPort, repository);
    }

    @Test
    void calculaYPersisteElAdnDelUsuario() {
        UserId userId = UserId.of(UUID.randomUUID());
        MusicDnaSignals signals = new MusicDnaSignals(3, 1, 2, 1, 3, 3, 2, 2, 0,
                LocalDate.now().minusYears(1), Instant.now().minusSeconds(3600));
        when(signalsPort.findSignals(userId)).thenReturn(signals);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserMusicDna result = useCase.execute(userId);

        assertEquals(userId, result.getUserId());
        verify(repository).save(any(UserMusicDna.class));
    }

    @Test
    void calculaAdnVacioSinContenidoDocumentado() {
        UserId userId = UserId.of(UUID.randomUUID());
        MusicDnaSignals signals = new MusicDnaSignals(0, 0, 0, 0, 0, 0, 0, 0, 0, null, Instant.now());
        when(signalsPort.findSignals(userId)).thenReturn(signals);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserMusicDna result = useCase.execute(userId);

        assertEquals("Sin datos suficientes", result.getSummaryPhrase());
    }
}
