package com.gresk.modules.tendencias.chronicle.application.usecase;

import com.gresk.modules.tendencias.chronicle.application.port.out.FeedFetcherPort;
import com.gresk.modules.tendencias.chronicle.application.port.out.FetchedFeedEntry;
import com.gresk.modules.tendencias.chronicle.domain.model.Chronicle;
import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleExcerpt;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.port.out.ChronicleRepositoryPort;
import com.gresk.modules.tendencias.chronicle.domain.port.out.FeedSourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Llamado por el scheduler ({@code IngestChroniclesJob}). Solo recorre fuentes
 * APPROVED — una fuente PENDING_APPROVAL nunca se pasa al fetcher, así que no
 * hay forma de que aparezca contenido de un medio no vetado.
 *
 * Dos casos borde manejados explícitamente:
 * - Feed sin {@code <description>}: se usa el título como fallback de extracto
 *   en vez de fallar (ChronicleExcerpt rechaza texto en blanco).
 * - Mismo artículo sindicado por dos fuentes distintas: el dedup por
 *   (feedSourceId, guid) no lo detecta, pero la constraint UNIQUE(link) en BD
 *   sí — se captura como "ya existe, skip" sin abortar el resto del batch.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IngestChroniclesUseCase {

    private final FeedSourceRepositoryPort feedSourceRepository;
    private final FeedFetcherPort fetcher;
    private final ChronicleRepositoryPort chronicleRepository;

    public void execute() {
        for (FeedSource source : feedSourceRepository.findAllApproved()) {
            try {
                ingestFrom(source);
                source.markFetched(Instant.now());
                feedSourceRepository.save(source);
            } catch (Exception e) {
                log.error("Feed ingestion failed for source {}: {}", source.getId(), e.getMessage());
                // una fuente caída no bloquea el resto
            }
        }
    }

    private void ingestFrom(FeedSource source) {
        for (FetchedFeedEntry entry : fetcher.fetch(source)) {
            if (chronicleRepository.existsByFeedSourceIdAndGuid(source.getId(), entry.guid())) continue;

            String rawSummary = entry.rawSummary() == null ? "" : entry.rawSummary();
            ChronicleExcerpt excerpt = ChronicleExcerpt.truncate(
                    rawSummary.isBlank() ? entry.title() : rawSummary);

            try {
                chronicleRepository.save(Chronicle.publish(
                        entry.title(), excerpt, entry.link(), entry.guid(),
                        source.attribution(), source.getId(), entry.publishedAt()));
            } catch (DataIntegrityViolationException dup) {
                log.debug("Chronicle with link {} already exists from another source, skipping", entry.link());
            }
        }
    }
}
