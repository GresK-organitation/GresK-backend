package com.gresk.modules.identity.infrastructure.web;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.identity.application.command.LoginCommand;
import com.gresk.modules.identity.application.command.RegisterPromoterAccountCommand;
import com.gresk.modules.identity.application.command.RegisterUserAccountCommand;
import com.gresk.modules.identity.application.port.in.LoginUseCase;
import com.gresk.modules.identity.application.port.in.RegisterUserAccountUseCase;
import com.gresk.modules.identity.application.usecase.GetEmailUseCase;
import com.gresk.modules.identity.application.usecase.RegisterPromoterAccountUseCase;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.modules.identity.infrastructure.web.dto.AuthResponse;
import com.gresk.modules.identity.infrastructure.web.dto.LoginRequest;
import com.gresk.modules.identity.infrastructure.web.dto.RegisterPromoterAuthRequest;
import com.gresk.modules.identity.infrastructure.web.dto.RegisterUserAuthRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserAccountUseCase registerUserAccountUseCase;
    private final RegisterPromoterAccountUseCase registerPromoterAccountUseCase;
    private final GetEmailUseCase getEmailUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/register/user")
    @Transactional
    public ResponseEntity<Map<String, String>> registerUser(
            @Valid @RequestBody RegisterUserAuthRequest request) {

        AccountId accountId = registerUserAccountUseCase.execute(
                RegisterUserAccountCommand.builder()
                        .email(request.email())
                        .rawPassword(request.password())
                        .name(request.name())
                        .description(request.description())
                        .city(request.city())
                        .musicGenres(request.musicGenres())
                        .build()
        );
        return ResponseEntity.status(201).body(Map.of(
                "accountId", accountId.value().toString()
        ));
    }

    @PostMapping("/register/promoter")
    @Transactional
    public ResponseEntity<Map<String, String>> registerPromoter(
            @Valid @RequestBody RegisterPromoterAuthRequest request) {

        AccountId accountId = registerPromoterAccountUseCase.execute(
                RegisterPromoterAccountCommand.builder()
                        .email(request.email())
                        .rawPassword(request.password())
                        .companyName(request.name())
                        .city(request.city())
                        .country(request.country())
                        .address(request.address())
                        .description(request.description())
                        .musicalGenres(request.musicalGenres())
                        .build()
        );

        return ResponseEntity.status(201).body(Map.of(
                "accountId", accountId.value().toString()
        ));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = getEmailUseCase.existsByEmail(email);

        return ResponseEntity.ok(Map.of("available", !exists));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthToken token = loginUseCase.execute(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(new AuthResponse(token.token(), token.expiresIn()));
    }
}
