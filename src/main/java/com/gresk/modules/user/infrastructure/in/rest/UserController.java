package com.gresk.modules.user.infrastructure.in.rest;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.user.application.dto.UserDashboardDTO;
import com.gresk.modules.user.domain.port.in.GetUserDashboardUseCase;
import com.gresk.modules.user.domain.port.in.UpdateUserAvatarUseCase;
import com.gresk.modules.user.domain.port.in.UpdateUserUseCase;
import com.gresk.modules.user.infrastructure.in.rest.dto.request.UpdateUserProfileRequest;
import com.gresk.modules.user.infrastructure.in.rest.dto.response.UserDashboardResponseDTO;
import com.gresk.modules.user.infrastructure.in.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UpdateUserUseCase updateUseCase;
    private final UpdateUserAvatarUseCase updateAvatarUseCase;
    private final GetUserDashboardUseCase getDashboardUseCase;
    private final SecurityContextService securityContextService;

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

    @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAvatar(@RequestPart("file") MultipartFile file) {
        UUID id = securityContextService.currentUserId();
        updateAvatarUseCase.execute(id, file);
        return ResponseEntity.noContent().build();
    }
}
