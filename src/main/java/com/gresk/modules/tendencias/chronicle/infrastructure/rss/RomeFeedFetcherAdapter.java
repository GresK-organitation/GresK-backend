package com.gresk.modules.tendencias.chronicle.infrastructure.rss;

import com.gresk.modules.tendencias.chronicle.application.port.out.FeedFetcherPort;
import com.gresk.modules.tendencias.chronicle.application.port.out.FetchedFeedEntry;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.util.List;

/**
 * Solo lee {@code entry.getDescription()} (el resumen que el propio feed
 * expone) — nunca hace una petición HTTP adicional al {@code link} del
 * artículo. Esta es la garantía estructural de que nunca se reproduce el
 * cuerpo completo del artículo, no solo una convención de código.
 */
@Component
@RequiredArgsConstructor
public class RomeFeedFetcherAdapter implements FeedFetcherPort {

    private final FeedSummarySanitizer sanitizer;
    private final TendenciasFeedProperties properties;

    @Override
    @RateLimiter(name = "rssFeed")
    @CircuitBreaker(name = "rssFeed")
    public List<FetchedFeedEntry> fetch(FeedSource source) {
        try (XmlReader reader = new XmlReader(URI.create(source.getFeedUrl()).toURL())) {
            SyndFeed feed = new SyndFeedInput().build(reader);
            return feed.getEntries().stream()
                    .limit(properties.maxEntriesPerFetch())
                    .map(this::toFetchedEntry)
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch feed " + source.getFeedUrl(), e);
        }
    }

    private FetchedFeedEntry toFetchedEntry(SyndEntry entry) {
        String guid = entry.getUri() != null && !entry.getUri().isBlank() ? entry.getUri() : entry.getLink();
        String rawSummary = entry.getDescription() != null ? entry.getDescription().getValue() : "";
        Instant publishedAt = entry.getPublishedDate() != null
                ? entry.getPublishedDate().toInstant()
                : Instant.now();
        return new FetchedFeedEntry(guid, entry.getTitle(), sanitizer.stripHtml(rawSummary), entry.getLink(), publishedAt);
    }
}
