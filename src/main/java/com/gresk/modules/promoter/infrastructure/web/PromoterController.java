package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.application.port.in.GetPromoterByAccountIdPort;
import com.gresk.modules.promoter.application.port.in.UpdatePromoterProfilePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/promoters")
@RequiredArgsConstructor
public class PromoterController {

    private final GetPromoterByAccountIdPort getByAccountIdUseCase;
    private final UpdatePromoterProfilePort updateUseCase;
    private final SecurityContextService securityContextService;

    @GetMapping("/me")
    public ResponseEntity<PromoterResponse> getMe() {
        UUID accountId = securityContextService.currentUserId();
        return ResponseEntity.ok(
                PromoterResponse.from(getByAccountIdUseCase.execute(accountId))
        );
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(@Valid @RequestBody UpdatePromoterProfileRequest request) {
        UUID accountId = securityContextService.currentUserId();
        var promoter = getByAccountIdUseCase.execute(accountId);
        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                promoter.getId().value().toString(),
                request.name(), request.city(), request.country(),
                request.address(), request.description(),
                request.musicalGenres()
        );
        updateUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
