package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.command.CreateArtistCommand;
import com.gresk.modules.artist.application.port.in.CreateArtistPort;
import com.gresk.modules.artist.application.port.in.GetArtistByIdPort;
import com.gresk.modules.artist.application.port.in.GetArtistsByPromoterPort;
import com.gresk.modules.artist.application.dto.SpotifyArtistSuggestionDTO;
import com.gresk.modules.artist.application.port.in.SearchSpotifyArtistsPort;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.rider.application.usecase.GetArtistRidersUseCase;
import com.gresk.modules.rider.infrastructure.web.RiderResponse;
import com.gresk.modules.rider.infrastructure.web.RiderResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final CreateArtistPort          createArtistUseCase;
    private final GetArtistsByPromoterPort  getArtistsByPromoterUseCase;
    private final GetArtistByIdPort         getArtistByIdUseCase;
    private final SearchSpotifyArtistsPort  searchSpotifyArtistsUseCase;
    private final GetArtistRidersUseCase    getArtistRidersUseCase;
    private final ArtistResponseMapper      mapper;
    private final RiderResponseMapper       riderMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ArtistResponse> create(
            @RequestPart("data") @Valid CreateArtistRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal String promoterId) {

        CreateArtistCommand command = new CreateArtistCommand(
                promoterId,
                request.name(),
                request.origin(),
                request.genres(),
                image,
                request.bio(),
                request.status(),
                request.fee(),
                request.followers(),
                request.tags(),
                request.contact(),
                request.instagramUrl(),
                request.spotifyUrl(),
                request.spotifyArtistId(),
                request.spotifyName(),
                request.spotifyImageUrl(),
                request.spotifyGenres()
        );

        Artist artist = createArtistUseCase.execute(command);
        ArtistResponse response = mapper.toResponse(artist);

        return ResponseEntity
                .created(URI.create("/api/v1/artists/" + artist.getId().value()))
                .body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<ArtistResponse>> listMyArtists(
            @AuthenticationPrincipal String promoterId) {

        List<ArtistResponse> artists = getArtistsByPromoterUseCase
                .execute(promoterId)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ArtistResponse> getById(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {

        return ResponseEntity.ok(
                mapper.toResponse(getArtistByIdUseCase.execute(id, promoterId))
        );
    }

    /**
     * Busca artistas en Spotify por nombre para que el promotor pueda vincular
     * su artista de GresK con el perfil correcto de Spotify.
     * Devuelve los top 5 resultados con id, nombre, imagen y géneros.
     */
    @GetMapping("/spotify/search")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<SpotifyArtistSuggestionDTO>> searchSpotifyArtist(
            @RequestParam String name) {

        return ResponseEntity.ok(searchSpotifyArtistsUseCase.execute(name));
    }

    @GetMapping("/{artistId}/riders")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<RiderResponse>> getArtistRiders(
            @PathVariable String artistId) {

        List<RiderResponse> riders = getArtistRidersUseCase.execute(artistId).stream()
                .map(riderMapper::toResponse)
                .toList();
        return ResponseEntity.ok(riders);
    }
}
