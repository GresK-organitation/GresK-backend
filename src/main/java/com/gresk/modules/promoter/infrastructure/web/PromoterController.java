package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.modules.promoter.application.command.AuthenticatePromoterCommand;
import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.application.query.GetPromoterQuery;
import com.gresk.modules.promoter.application.usecase.AuthenticatePromoterUseCase;
import com.gresk.modules.promoter.application.usecase.GetPromoterUseCase;
import com.gresk.modules.promoter.application.usecase.RegisterPromoterUseCase;
import com.gresk.modules.promoter.application.usecase.UpdatePromoterProfileUseCase;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/promoters")
@RequiredArgsConstructor
public class PromoterController {

    private final RegisterPromoterUseCase registerUseCase;
    private final AuthenticatePromoterUseCase authenticateUseCase;
    private final GetPromoterUseCase getUseCase;
    private final UpdatePromoterProfileUseCase updateUseCase;

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, String>>> register(
            @Valid @RequestBody RegisterPromoterRequest request) {
        RegisterPromoterCommand command = new RegisterPromoterCommand(
                request.email(), request.password(), request.name(),
                request.city(), request.country(), request.address(),
                request.description(), request.musicalGenres()
        );
        return registerUseCase.execute(command)
                .map(id -> ResponseEntity.status(201)
                        .<Map<String, String>>body(Map.of("id", id.value().toString())));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(
            @Valid @RequestBody LoginPromoterRequest request) {
        AuthenticatePromoterCommand command = new AuthenticatePromoterCommand(
                request.email(), request.password()
        );
        return authenticateUseCase.execute(command)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<PromoterResponse>> getMe() {
        return currentPromoterId()
                .flatMap(id -> getUseCase.execute(new GetPromoterQuery(id.value().toString())))
                .map(promoter -> ResponseEntity.ok(PromoterResponse.from(promoter)));
    }

    @PutMapping("/me")
    public Mono<ResponseEntity<Void>> updateMe(
            @Valid @RequestBody UpdatePromoterProfileRequest request) {
        return currentPromoterId()
                .flatMap(id -> {
                    UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                            id.value().toString(),
                            request.name(), request.city(), request.country(),
                            request.address(), request.description(),
                            request.musicalGenres()
                    );
                    return updateUseCase.execute(command);
                })
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }

    // --- helpers ---

    private Mono<PromoterId> currentPromoterId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> (PromoterId) auth.getPrincipal());
    }
}
