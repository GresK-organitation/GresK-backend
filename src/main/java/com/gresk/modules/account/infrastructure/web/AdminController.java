package com.gresk.modules.account.infrastructure.web;

import com.gresk.modules.account.application.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ActivateAccountUseCase activateAccountUseCase;
    private final ApprovePromoterUseCase approvePromoterUseCase;
    private final SuspendAccountUseCase suspendAccountUseCase;

    @PatchMapping("/promoter/{id}/active")
    public ResponseEntity<Void> activeAccount(@PathVariable UUID id) {
        activateAccountUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/account/{id}/suspend")
    public ResponseEntity<Void> suspendAccount(@PathVariable UUID id) {
        suspendAccountUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/account/{id}/approve")
    public ResponseEntity<Void> approvePromoter(@PathVariable UUID id) {
        approvePromoterUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}