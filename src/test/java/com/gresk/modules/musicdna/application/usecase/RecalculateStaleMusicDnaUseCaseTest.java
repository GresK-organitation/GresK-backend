package com.gresk.modules.musicdna.application.usecase;

import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignalsPort;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecalculateStaleMusicDnaUseCaseTest {

    @Mock private MusicDnaSignalsPort          signalsPort;
    @Mock private CalculateUserMusicDnaUseCase calculateUserMusicDnaUseCase;

    private RecalculateStaleMusicDnaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RecalculateStaleMusicDnaUseCase(signalsPort, calculateUserMusicDnaUseCase);
    }

    @Test
    void unFalloAisladoNoBloqueaElRestoDelBatch() {
        UserId user1 = UserId.of(UUID.randomUUID());
        UserId user2 = UserId.of(UUID.randomUUID());
        when(signalsPort.findUserIdsWithActivitySince(any())).thenReturn(List.of(user1, user2));
        when(calculateUserMusicDnaUseCase.execute(user1)).thenThrow(new RuntimeException("boom"));

        useCase.execute();

        verify(calculateUserMusicDnaUseCase).execute(user1);
        verify(calculateUserMusicDnaUseCase).execute(user2);
    }
}
