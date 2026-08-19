package com.gresk.modules.musicdna.infrastructure.web;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.musicdna.application.usecase.GetMyMusicDnaUseCase;
import com.gresk.modules.musicdna.application.usecase.GetUserMusicDnaUseCase;
import com.gresk.modules.musicdna.domain.exception.MusicDnaNotFoundException;
import com.gresk.modules.user.domain.model.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Music DNA", description = "Perfil de 6 dimensiones de cómo el usuario vive la música en directo")
public class MusicDnaController {

    private final GetMyMusicDnaUseCase       getMyMusicDnaUseCase;
    private final GetUserMusicDnaUseCase     getUserMusicDnaUseCase;
    private final SecurityContextService     securityContextService;
    private final UserMusicDnaResponseMapper mapper;

    @GetMapping("/me/music-dna")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "ADN Musical del usuario autenticado")
    public ResponseEntity<UserMusicDnaResponse> getMine() {
        UserId userId = UserId.of(securityContextService.currentUserId());

        return getMyMusicDnaUseCase.execute(userId)
                .map(dna -> ResponseEntity.ok(mapper.toResponse(dna)))
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{userId}/music-dna")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "ADN Musical de otro usuario (visible a cualquier usuario autenticado)")
    public ResponseEntity<UserMusicDnaResponse> getOther(@PathVariable String userId) {
        return getUserMusicDnaUseCase.execute(UserId.from(userId))
                .map(dna -> ResponseEntity.ok(mapper.toResponse(dna)))
                .orElseThrow(() -> new MusicDnaNotFoundException("Music DNA not found for user: " + userId));
    }
}
