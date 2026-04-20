package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.command.CreateArtistCommand;
import com.gresk.modules.artist.application.port.in.CreateArtistPort;
import com.gresk.modules.artist.application.port.in.GetArtistByIdPort;
import com.gresk.modules.artist.application.port.in.GetArtistsByPromoterPort;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final CreateArtistPort          createArtistUseCase;
    private final GetArtistsByPromoterPort  getArtistsByPromoterUseCase;
    private final GetArtistByIdPort         getArtistByIdUseCase;
    private final ArtistResponseMapper      mapper;

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ArtistResponse> create(
            @Valid @RequestBody CreateArtistRequest request,
            @AuthenticationPrincipal String promoterId) {

        CreateArtistCommand command = new CreateArtistCommand(
                promoterId,
                request.name(),
                request.origin(),
                request.genres(),
                request.imageUrl(),
                request.bio(),
                request.status(),
                request.fee(),
                request.followers(),
                request.tags(),
                request.contact(),
                request.instagramUrl(),
                request.spotifyUrl()
        );

        ArtistId artistId = createArtistUseCase.execute(command);
        ArtistResponse response = mapper.toResponse(
                getArtistByIdUseCase.execute(artistId.value().toString(), promoterId)
        );

        return ResponseEntity
                .created(URI.create("/api/v1/artists/" + artistId.value()))
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
}
