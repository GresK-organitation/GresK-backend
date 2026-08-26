package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.DemandSignal;
import com.gresk.modules.discovery.domain.port.out.DemandSignalRepository;
import com.gresk.modules.discovery.domain.port.out.UserProfilePort;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleDemandSignalUseCaseTest {

    @Mock private DemandSignalRepository demandSignalRepository;
    @Mock private UserProfilePort userProfilePort;

    private ToggleDemandSignalUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ToggleDemandSignalUseCase(demandSignalRepository, userProfilePort);
    }

    @Test
    void execute_creaLaSenalCuandoNoExiste() {
        ArtistId artistId = ArtistId.generate();
        UserId userId = UserId.of(UUID.randomUUID());
        when(demandSignalRepository.findByArtistAndUser(artistId, userId)).thenReturn(Optional.empty());
        when(userProfilePort.findCityByUserId(userId)).thenReturn(Optional.of("Barcelona"));

        boolean active = useCase.execute(artistId, userId);

        assertTrue(active);
        verify(demandSignalRepository).save(any(DemandSignal.class));
    }

    @Test
    void execute_retiraLaSenalCuandoYaExiste() {
        ArtistId artistId = ArtistId.generate();
        UserId userId = UserId.of(UUID.randomUUID());
        DemandSignal existing = DemandSignal.create(artistId, userId, "Barcelona");
        when(demandSignalRepository.findByArtistAndUser(artistId, userId)).thenReturn(Optional.of(existing));

        boolean active = useCase.execute(artistId, userId);

        assertFalse(active);
        verify(demandSignalRepository).delete(existing);
    }
}
