package com.gresk.modules.artist.infrastructure.web;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.artist.application.command.RegisterArtistCommand;
import com.gresk.modules.artist.application.command.UpdateArtistCommand;
import com.gresk.modules.artist.application.dto.ArtistResponse;
import com.gresk.modules.artist.application.port.in.DeleteArtistPort;
import com.gresk.modules.artist.application.port.in.GetArtistPort;
import com.gresk.modules.artist.application.port.in.ListPromoterArtistsPort;
import com.gresk.modules.artist.application.port.in.RegisterArtistPort;
import com.gresk.modules.artist.application.port.in.UpdateArtistPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final RegisterArtistPort      registerArtist;
    private final GetArtistPort           getArtist;
    private final ListPromoterArtistsPort listArtists;
    private final UpdateArtistPort        updateArtist;
    private final DeleteArtistPort        deleteArtist;
    private final SecurityContextService  securityContextService;

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody CreateArtistRequest request) {
        UUID promoterId = securityContextService.currentUserId();
        RegisterArtistCommand command = new RegisterArtistCommand(
                promoterId.toString(),
                request.name(), request.origin(), request.genres(), request.imageUrl(),
                request.bio(), request.status(), request.fee(), request.followers(),
                request.contact(), request.socialSpotify(), request.socialInstagram(), request.tags()
        );
        return ResponseEntity.status(201).body(registerArtist.execute(command));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<ArtistResponse>> listMine() {
        UUID promoterId = securityContextService.currentUserId();
        return ResponseEntity.ok(listArtists.execute(promoterId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ArtistResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(getArtist.execute(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ArtistResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateArtistRequest request) {
        UUID promoterId = securityContextService.currentUserId();
        UpdateArtistCommand command = new UpdateArtistCommand(
                id, promoterId.toString(),
                request.name(), request.origin(), request.genres(), request.imageUrl(),
                request.bio(), request.status(), request.fee(), request.followers(),
                request.contact(), request.socialSpotify(), request.socialInstagram(), request.tags()
        );
        return ResponseEntity.ok(updateArtist.execute(command));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        UUID promoterId = securityContextService.currentUserId();
        deleteArtist.execute(id, promoterId);
        return ResponseEntity.noContent().build();
    }
}
