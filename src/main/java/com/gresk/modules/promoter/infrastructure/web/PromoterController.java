package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.application.dto.PromoterDashboardDTO;
import com.gresk.modules.promoter.application.port.in.GetPromoterByAccountIdPort;
import com.gresk.modules.promoter.application.port.in.GetPromoterDashboardPort;
import com.gresk.modules.promoter.application.port.in.UpdatePromoterLogoPort;
import com.gresk.modules.promoter.application.port.in.UpdatePromoterProfilePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/promoters")
@RequiredArgsConstructor
public class PromoterController {

    private final GetPromoterByAccountIdPort getByAccountIdUseCase;
    private final GetPromoterDashboardPort getDashboardUseCase;
    private final UpdatePromoterProfilePort updateUseCase;
    private final UpdatePromoterLogoPort updateLogoUseCase;
    private final SecurityContextService securityContextService;

    @GetMapping("/me")
    public ResponseEntity<PromoterResponse> getMe() {
        UUID accountId = securityContextService.currentUserId();
        return ResponseEntity.ok(PromoterResponse.from(getByAccountIdUseCase.execute(accountId)));
    }

    @GetMapping("/me/dashboard")
    public ResponseEntity<PromoterDashboardDTO> getDashboard() {
        UUID accountId = securityContextService.currentUserId();
        return ResponseEntity.ok(getDashboardUseCase.execute(accountId));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(@Valid @RequestBody UpdatePromoterProfileRequest request) {
        UUID accountId = securityContextService.currentUserId();
        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                accountId.toString(),
                request.name(),
                request.address(),
                request.city(),
                request.country(),
                request.description(),
                request.musicalGenres()
        );
        updateUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/me/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateLogo(@RequestPart("file") MultipartFile file) {
        UUID accountId = securityContextService.currentUserId();
        updateLogoUseCase.execute(accountId, file);
        return ResponseEntity.noContent().build();
    }
}
