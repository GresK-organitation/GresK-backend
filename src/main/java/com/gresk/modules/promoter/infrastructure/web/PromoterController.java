package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.modules.promoter.application.command.AuthenticatePromoterCommand;
import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.application.query.GetPromoterQuery;
import com.gresk.modules.promoter.application.port.in.AuthenticatePromoterPort;
import com.gresk.modules.promoter.application.port.in.GetPromoterPort;
import com.gresk.modules.promoter.application.port.in.RegisterPromoterPort;
import com.gresk.modules.promoter.application.port.in.UpdatePromoterProfilePort;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/promoters")
@RequiredArgsConstructor
public class PromoterController {

    private final RegisterPromoterPort registerUseCase;
    private final AuthenticatePromoterPort authenticateUseCase;
    private final GetPromoterPort getUseCase;
    private final UpdatePromoterProfilePort updateUseCase;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterPromoterRequest request) {
        RegisterPromoterCommand command = new RegisterPromoterCommand(
                request.email(), request.password(), request.name(),
                request.city(), request.country(), request.address(),
                request.description(), request.musicalGenres()
        );
        PromoterId id = registerUseCase.execute(command);
        return ResponseEntity.status(201).body(Map.of("id", id.value().toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginPromoterRequest request) {
        AuthenticatePromoterCommand command = new AuthenticatePromoterCommand(
                request.email(), request.password()
        );
        return ResponseEntity.ok(authenticateUseCase.execute(command));
    }

    @GetMapping("/me")
    public ResponseEntity<PromoterResponse> getMe(
            @AuthenticationPrincipal PromoterId promoterId) {
        return ResponseEntity.ok(
                PromoterResponse.from(getUseCase.execute(new GetPromoterQuery(promoterId.value().toString())))
        );
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(
            @Valid @RequestBody UpdatePromoterProfileRequest request,
            @AuthenticationPrincipal PromoterId promoterId) {
        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                promoterId.value().toString(),
                request.name(), request.city(), request.country(),
                request.address(), request.description(),
                request.musicalGenres()
        );
        updateUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
