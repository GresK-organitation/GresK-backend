package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.application.command.AddListItemCommand;
import com.gresk.modules.curation.domain.exception.DuplicateListItemException;
import com.gresk.modules.curation.domain.exception.ReferencedEntryNotFoundException;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.ListVisibility;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.curation.domain.port.out.JournalEntryLookupPort;
import com.gresk.modules.curation.domain.port.out.ReviewLookupPort;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddListItemUseCaseTest {

    @Mock private CuratedListRepository  repository;
    @Mock private ReviewLookupPort       reviewLookupPort;
    @Mock private JournalEntryLookupPort journalEntryLookupPort;

    private final UserId ownerId = UserId.of(UUID.randomUUID());
    private AddListItemUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddListItemUseCase(repository, new CuratedListAccessGuard(repository), reviewLookupPort, journalEntryLookupPort);
    }

    private CuratedList newList() {
        return CuratedList.create(ownerId, "Mejores conciertos de jazz 2025", null, ListVisibility.PRIVATE);
    }

    @Test
    void anadeUnaReseñaVerificadaExistente() {
        CuratedList list = newList();
        UUID reviewId = UUID.randomUUID();
        when(repository.findById(list.getId())).thenReturn(Optional.of(list));
        when(reviewLookupPort.existsById(reviewId)).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CuratedList result = useCase.execute(new AddListItemCommand(
                list.getId().toString(), ownerId.toString(), "VERIFIED_REVIEW", reviewId.toString()));

        assertEquals(1, result.getItems().size());
    }

    @Test
    void rechazaUnaEntradaDeJournalInexistente() {
        CuratedList list = newList();
        UUID entryId = UUID.randomUUID();
        when(repository.findById(list.getId())).thenReturn(Optional.of(list));
        when(journalEntryLookupPort.existsById(entryId)).thenReturn(false);

        assertThrows(ReferencedEntryNotFoundException.class, () -> useCase.execute(new AddListItemCommand(
                list.getId().toString(), ownerId.toString(), "JOURNAL_ENTRY", entryId.toString())));
    }

    @Test
    void rechazaAnadirElMismoItemDosVeces() {
        CuratedList list = newList();
        UUID reviewId = UUID.randomUUID();
        list.addItem(com.gresk.modules.curation.domain.model.ListedEntryType.VERIFIED_REVIEW, reviewId);
        when(repository.findById(list.getId())).thenReturn(Optional.of(list));
        when(reviewLookupPort.existsById(reviewId)).thenReturn(true);

        assertThrows(DuplicateListItemException.class, () -> useCase.execute(new AddListItemCommand(
                list.getId().toString(), ownerId.toString(), "VERIFIED_REVIEW", reviewId.toString())));
    }
}
