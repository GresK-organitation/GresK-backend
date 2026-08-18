package com.gresk.modules.tendencias.chronicle.application.usecase;

import com.gresk.modules.tendencias.chronicle.application.port.out.FeedFetcherPort;
import com.gresk.modules.tendencias.chronicle.application.port.out.FetchedFeedEntry;
import com.gresk.modules.tendencias.chronicle.domain.model.Chronicle;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceStatus;
import com.gresk.modules.tendencias.chronicle.domain.port.out.ChronicleRepositoryPort;
import com.gresk.modules.tendencias.chronicle.domain.port.out.FeedSourceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestChroniclesUseCaseTest {

    @Mock
    private FeedSourceRepositoryPort feedSourceRepository;
    @Mock
    private FeedFetcherPort fetcher;
    @Mock
    private ChronicleRepositoryPort chronicleRepository;

    private IngestChroniclesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new IngestChroniclesUseCase(feedSourceRepository, fetcher, chronicleRepository);
    }

    @Test
    void soloSeConsultanFuentesApproved() {
        when(feedSourceRepository.findAllApproved()).thenReturn(List.of());

        useCase.execute();

        verify(fetcher, never()).fetch(any());
        verify(feedSourceRepository, never()).findAll();
    }

    @Test
    void elMismoGuidDosVecesSoloPersisteUnaCronica() {
        FeedSource source = FeedSource.register("Medio", "https://medio.example/feed", null);
        source.approve(java.util.UUID.randomUUID());
        when(feedSourceRepository.findAllApproved()).thenReturn(List.of(source));

        FetchedFeedEntry entry = new FetchedFeedEntry("guid-1", "Título", "Un resumen.", "https://medio.example/a", Instant.now());
        when(fetcher.fetch(source)).thenReturn(List.of(entry));
        when(chronicleRepository.existsByFeedSourceIdAndGuid(eq(source.getId()), eq("guid-1")))
                .thenReturn(false)
                .thenReturn(true);

        useCase.execute();
        useCase.execute();

        verify(chronicleRepository, times(1)).save(any(Chronicle.class));
    }

    @Test
    void unResumenVacioUsaElTituloComoFallback() {
        FeedSource source = FeedSource.register("Medio", "https://medio.example/feed", null);
        source.approve(java.util.UUID.randomUUID());
        when(feedSourceRepository.findAllApproved()).thenReturn(List.of(source));

        FetchedFeedEntry entry = new FetchedFeedEntry("guid-2", "Título como fallback", "", "https://medio.example/b", Instant.now());
        when(fetcher.fetch(source)).thenReturn(List.of(entry));
        when(chronicleRepository.existsByFeedSourceIdAndGuid(any(), eq("guid-2"))).thenReturn(false);

        useCase.execute();

        verify(chronicleRepository).save(argThat(chronicle ->
                chronicle.getExcerpt().value().equals("Título como fallback")));
    }

    @Test
    void unaColisionDeLinkEntreFuentesDistintasNoAbortaElBatch() {
        FeedSource source = FeedSource.register("Medio", "https://medio.example/feed", null);
        source.approve(java.util.UUID.randomUUID());
        when(feedSourceRepository.findAllApproved()).thenReturn(List.of(source));

        FetchedFeedEntry entry1 = new FetchedFeedEntry("guid-3", "Título 1", "Resumen 1", "https://medio.example/dup", Instant.now());
        FetchedFeedEntry entry2 = new FetchedFeedEntry("guid-4", "Título 2", "Resumen 2", "https://medio.example/c", Instant.now());
        when(fetcher.fetch(source)).thenReturn(List.of(entry1, entry2));
        when(chronicleRepository.existsByFeedSourceIdAndGuid(any(), any())).thenReturn(false);
        doThrow(new DataIntegrityViolationException("duplicate link"))
                .when(chronicleRepository).save(argThat(c -> c.getGuid().equals("guid-3")));

        useCase.execute();

        verify(chronicleRepository, times(2)).save(any(Chronicle.class));
        verify(feedSourceRepository).save(source);
    }
}
