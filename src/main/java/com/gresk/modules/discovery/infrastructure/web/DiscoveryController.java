package com.gresk.modules.discovery.infrastructure.web;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.application.usecase.BuildSurpriseMeFiltersUseCase;
import com.gresk.modules.discovery.application.usecase.GetArtistConnectionsUseCase;
import com.gresk.modules.discovery.application.usecase.GetArtistDiscoveryDetailUseCase;
import com.gresk.modules.discovery.application.usecase.GetBeforeAnyoneUseCase;
import com.gresk.modules.discovery.application.usecase.SearchDiscoveryArtistsUseCase;
import com.gresk.modules.discovery.application.usecase.ToggleDemandSignalUseCase;
import com.gresk.modules.discovery.domain.port.out.DiscoveryFilters;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.application.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/discovery")
@RequiredArgsConstructor
@Tag(name = "Discovery", description = "Descubrimiento de artistas emergentes con comunidad real en GresK")
public class DiscoveryController {

    private final SearchDiscoveryArtistsUseCase searchDiscoveryArtistsUseCase;
    private final GetArtistDiscoveryDetailUseCase getArtistDiscoveryDetailUseCase;
    private final ToggleDemandSignalUseCase toggleDemandSignalUseCase;
    private final GetArtistConnectionsUseCase getArtistConnectionsUseCase;
    private final GetBeforeAnyoneUseCase getBeforeAnyoneUseCase;
    private final BuildSurpriseMeFiltersUseCase buildSurpriseMeFiltersUseCase;
    private final DiscoveryFilterQueryMapper filterQueryMapper;
    private final DiscoveryResponseMapper responseMapper;
    private final SecurityContextService securityContextService;

    @GetMapping("/artists")
    @Operation(summary = "Busca artistas emergentes con filtros de tamaño, ubicación, actividad en vivo, género y comunidad")
    public ResponseEntity<PageResponse<DiscoveryArtistSummaryResponse>> search(
            @RequestParam(required = false) Set<String> sizeTiers,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Set<String> genres,
            @RequestParam(required = false) Set<String> excludeGenres,
            @RequestParam(required = false) String liveFilter,
            @RequestParam(required = false) String myCity,
            @RequestParam(defaultValue = "false") boolean onlyWithReviews,
            @RequestParam(defaultValue = "false") boolean onlyWithDemandInMyCity,
            @RequestParam(defaultValue = "false") boolean onlyNewOnPlatform,
            @RequestParam(defaultValue = "false") boolean onlyWithMusicBrainz,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        DiscoveryFilters filters = filterQueryMapper.toFilters(sizeTiers, city, country, genres, excludeGenres,
                liveFilter, myCity, onlyWithReviews, onlyWithDemandInMyCity, onlyNewOnPlatform, onlyWithMusicBrainz,
                sort, page, size);

        var result = searchDiscoveryArtistsUseCase.execute(filters);
        return ResponseEntity.ok(toResponse(result));
    }

    @GetMapping("/artists/{artistId}")
    @Operation(summary = "Ficha completa de un artista emergente")
    public ResponseEntity<ArtistDiscoveryDetailResponse> getDetail(@PathVariable String artistId) {
        var detail = getArtistDiscoveryDetailUseCase.execute(ArtistId.of(artistId));
        return ResponseEntity.ok(responseMapper.toResponse(detail));
    }

    @PostMapping("/artists/{artistId}/demand")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "\"Quiero que vengan a mi ciudad\" — toggle de señal de demanda")
    public ResponseEntity<DemandSignalResponse> toggleDemand(@PathVariable String artistId) {
        UserId userId = UserId.of(securityContextService.currentUserId());
        boolean active = toggleDemandSignalUseCase.execute(ArtistId.of(artistId), userId);
        return ResponseEntity.ok(new DemandSignalResponse(active));
    }

    @GetMapping("/artists/{artistId}/connections")
    @Operation(summary = "Modo Conexiones: artistas relacionados por co-reseña de la comunidad")
    public ResponseEntity<List<ArtistConnectionResponse>> getConnections(@PathVariable String artistId) {
        var connections = getArtistConnectionsUseCase.execute(ArtistId.of(artistId));
        return ResponseEntity.ok(connections.stream().map(responseMapper::toResponse).toList());
    }

    @GetMapping("/before-anyone")
    @Operation(summary = "Modo Antes que nadie: artistas ordenados por fecha de entrada al catálogo GresK")
    public ResponseEntity<PageResponse<DiscoveryArtistSummaryResponse>> beforeAnyone(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(toResponse(getBeforeAnyoneUseCase.execute(page, size)));
    }

    @GetMapping("/surprise-me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Sorpréndeme: artistas pequeños con concierto próximo fuera de tus géneros habituales")
    public ResponseEntity<PageResponse<DiscoveryArtistSummaryResponse>> surpriseMe() {
        UserId userId = UserId.of(securityContextService.currentUserId());
        DiscoveryFilters filters = buildSurpriseMeFiltersUseCase.execute(userId);
        return ResponseEntity.ok(toResponse(searchDiscoveryArtistsUseCase.execute(filters)));
    }

    private PageResponse<DiscoveryArtistSummaryResponse> toResponse(
            PageResponse<com.gresk.modules.discovery.domain.port.out.DiscoveryArtistSummary> page) {
        return new PageResponse<>(
                page.content().stream().map(responseMapper::toResponse).toList(),
                page.totalElements(), page.page(), page.size(), page.totalPages()
        );
    }
}
