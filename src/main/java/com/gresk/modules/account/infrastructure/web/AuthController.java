package com.gresk.modules.account.infrastructure.web;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.account.application.command.LoginCommand;
import com.gresk.modules.account.application.command.RegisterPromoterAccountCommand;
import com.gresk.modules.account.application.command.RegisterUserAccountCommand;
import com.gresk.modules.account.application.port.in.LoginUseCase;
import com.gresk.modules.account.application.port.in.RegisterUserAccountUseCase;
import com.gresk.modules.account.application.usecase.GetEmailUseCase;
import com.gresk.modules.account.application.usecase.RegisterPromoterAccountUseCase;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.account.infrastructure.web.dto.AuthResponse;
import com.gresk.modules.account.infrastructure.web.dto.LoginRequest;
import com.gresk.modules.account.infrastructure.web.dto.RegisterPromoterAuthRequest;
import com.gresk.modules.account.infrastructure.web.dto.RegisterUserAuthRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserAccountUseCase registerUserAccountUseCase;
    private final RegisterPromoterAccountUseCase registerPromoterAccountUseCase;
    private final GetEmailUseCase getEmailUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping(value = "/register/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> registerUser(
            @Valid @RequestPart("data") RegisterUserAuthRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        AccountId accountId = registerUserAccountUseCase.execute(
                RegisterUserAccountCommand.builder()
                        .email(request.email())
                        .rawPassword(request.password())
                        .name(request.name())
                        .description(request.description())
                        .city(request.city())
                        .musicGenres(request.musicGenres())
                        .avatar(avatar)
                        .build()
        );
        return ResponseEntity.status(201).body(Map.of(
                "accountId", accountId.value().toString()
        ));
    }

    @PostMapping(value = "/register/promoter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> registerPromoter(
            @Valid @RequestPart("data") RegisterPromoterAuthRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {

        AccountId accountId = registerPromoterAccountUseCase.execute(
                RegisterPromoterAccountCommand.builder()
                        .email(request.email())
                        .rawPassword(request.password())
                        .companyName(request.name())
                        .street(request.street())
                        .city(request.city())
                        .country(request.country())
                        .description(request.description())
                        .musicalGenres(request.musicalGenres())
                        .logo(logo)
                        .phone(request.phone())
                        .website(request.website())
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
