package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.exception.CuratedListForbiddenException;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.ListVisibility;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.curation.domain.port.out.ListFollowerRepository;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowListUseCaseTest {

    @Mock private ListFollowerRepository followerRepository;
    @Mock private CuratedListRepository  listRepository;

    private final UserId ownerId    = UserId.of(UUID.randomUUID());
    private final UserId followerId = UserId.of(UUID.randomUUID());

    private FollowListUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FollowListUseCase(followerRepository, new CuratedListAccessGuard(listRepository));
    }

    @Test
    void siguePermiteSeguirUnaListaPublica() {
        CuratedList list = CuratedList.create(ownerId, "Salas pequeñas BCN", null, ListVisibility.PUBLIC);
        when(listRepository.findById(list.getId())).thenReturn(Optional.of(list));

        useCase.execute(list.getId().toString(), followerId.toString());

        verify(followerRepository).follow(list.getId(), followerId);
    }

    @Test
    void rechazaSeguirUnaListaPrivadaDeOtroUsuario() {
        CuratedList list = CuratedList.create(ownerId, "Notas privadas", null, ListVisibility.PRIVATE);
        when(listRepository.findById(list.getId())).thenReturn(Optional.of(list));

        assertThrows(CuratedListForbiddenException.class, () ->
                useCase.execute(list.getId().toString(), followerId.toString()));
    }
}
