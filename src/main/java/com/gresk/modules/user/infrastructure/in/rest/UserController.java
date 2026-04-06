package com.gresk.modules.user.infrastructure.in.rest;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.user.application.dto.UserDashboardDTO;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.in.GetUserDashboardUseCase;
import com.gresk.modules.user.domain.port.in.RegisterUserUseCase;
import com.gresk.modules.user.domain.port.in.UpdateUserUseCase;
import com.gresk.modules.user.infrastructure.in.rest.dto.request.RegisterUserRequest;
import com.gresk.modules.user.infrastructure.in.rest.dto.request.UpdateUserProfileRequest;
import com.gresk.modules.user.infrastructure.in.rest.dto.response.UserDashboardResponseDTO;
import com.gresk.modules.user.infrastructure.in.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterUserUseCase registerUseCase;
    private final UpdateUserUseCase updateUseCase;
    private final GetUserDashboardUseCase getDashboardUseCase;
    private final SecurityContextService securityContextService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder ucb
    ) {
        UserId userId = registerUseCase.execute(UserRestMapper.toRegisterUserCommand(request));

        URI location = ucb.path("/api/v1/users/{id}")
                .buildAndExpand(userId.value())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/me/dashboard")
    public ResponseEntity<UserDashboardResponseDTO> getDashboard() {
        UUID id = securityContextService.currentUserId();
        UserDashboardDTO dto = getDashboardUseCase.execute(id);
        return ResponseEntity.ok(UserDashboardResponseDTO.from(dto));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(@Valid @RequestBody UpdateUserProfileRequest request) {
        UUID id = securityContextService.currentUserId();
        updateUseCase.execute(id, UserRestMapper.toUpdateUsercommand(request));
        return ResponseEntity.noContent().build();
    }
}