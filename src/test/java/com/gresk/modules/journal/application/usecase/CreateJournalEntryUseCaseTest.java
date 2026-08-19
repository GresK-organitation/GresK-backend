package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.journal.application.command.CreateJournalEntryCommand;
import com.gresk.modules.journal.application.command.RatingCriterionInput;
import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntrySource;
import com.gresk.modules.journal.domain.model.JournalVisibility;
import com.gresk.modules.journal.domain.port.out.ArtistLookupPort;
import com.gresk.modules.journal.domain.port.out.EventLookupPort;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateJournalEntryUseCaseTest {

    @Mock private JournalEntryRepository    repository;
    @Mock private ArtistLookupPort          artistLookupPort;
    @Mock private EventLookupPort           eventLookupPort;
    @Mock private ApplicationEventPublisher eventPublisher;

    private CreateJournalEntryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateJournalEntryUseCase(repository, new CatalogLinkResolver(artistLookupPort, eventLookupPort), eventPublisher);
    }

    private CreateJournalEntryCommand command(String artistName, String artistId) {
        return new CreateJournalEntryCommand(
                UUID.randomUUID().toString(), artistName, artistId,
                LocalDate.of(2019, 1, 1), "YEAR", "Sala Apolo", "Barcelona", null,
                "Gran concierto", List.of(new RatingCriterionInput("luces", 5)),
                null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL
        );
    }

    @Test
    void creaUnaEntradaConArtistaLibreYLaPersiste() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JournalEntry result = useCase.execute(command("Radiohead", null));

        assertEquals("Radiohead", result.getArtistNameFree());
        verify(repository).save(any(JournalEntry.class));
    }

    @Test
    void rechazaArtistaDeCatalogoInexistente() {
        String artistId = UUID.randomUUID().toString();
        when(artistLookupPort.existsById(ArtistId.of(artistId))).thenReturn(false);

        assertThrows(InvalidJournalEntryException.class, () -> useCase.execute(command(null, artistId)));
    }

    @Test
    void aceptaArtistaDeCatalogoExistente() {
        String artistId = UUID.randomUUID().toString();
        when(artistLookupPort.existsById(ArtistId.of(artistId))).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JournalEntry result = useCase.execute(command(null, artistId));

        assertEquals(ArtistId.of(artistId), result.getArtistId());
    }
}
