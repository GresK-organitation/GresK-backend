package com.gresk.modules.identity.infrastructure.web;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.identity.application.command.LoginCommand;
import com.gresk.modules.identity.application.command.RegisterAccountCommand;
import com.gresk.modules.identity.application.port.in.LoginUseCase;
import com.gresk.modules.identity.application.port.in.RegisterAccountUseCase;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.modules.identity.infrastructure.web.dto.AuthResponse;
import com.gresk.modules.identity.infrastructure.web.dto.LoginRequest;
import com.gresk.modules.identity.infrastructure.web.dto.RegisterPromoterAuthRequest;
import com.gresk.modules.identity.infrastructure.web.dto.RegisterUserAuthRequest;
import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.application.port.in.RegisterPromoterPort;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.modules.user.application.command.RegisterUserCommand;
import com.gresk.modules.user.domain.port.in.RegisterUserUseCase;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterAccountUseCase registerAccountUseCase;
    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RegisterPromoterPort registerPromoterUseCase;

    @PostMapping("/register/user")
    @Transactional
    public ResponseEntity<Map<String, String>> registerUser(
            @Valid @RequestBody RegisterUserAuthRequest request) {

        AccountId accountId = registerAccountUseCase.execute(
                new RegisterAccountCommand(request.email(), request.password(), Set.of(Role.USER))
        );

        UserId userId = registerUserUseCase.execute(new RegisterUserCommand(
                request.email(),
                request.name(),
                request.description(),
                request.city(),
                request.musicGenres() != null ? request.musicGenres() : Set.of()
        ));

        return ResponseEntity.status(201).body(Map.of(
                "accountId", accountId.value().toString(),
                "userId", userId.value().toString()
        ));
    }

    @PostMapping("/register/promoter")
    @Transactional
    public ResponseEntity<Map<String, String>> registerPromoter(
            @Valid @RequestBody RegisterPromoterAuthRequest request) {

        AccountId accountId = registerAccountUseCase.execute(
                new RegisterAccountCommand(request.email(), request.password(), Set.of(Role.PROMOTER))
        );

        PromoterId promoterId = registerPromoterUseCase.execute(new RegisterPromoterCommand(
                accountId.value(),
                request.email(),
                request.name(),
                request.city(),
                request.country(),
                request.address(),
                request.description(),
                request.musicalGenres()
        ));

        return ResponseEntity.status(201).body(Map.of(
                "accountId", accountId.value().toString(),
                "promoterId", promoterId.value().toString()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthToken token = loginUseCase.execute(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(new AuthResponse(token.token(), token.expiresIn()));
    }
}
