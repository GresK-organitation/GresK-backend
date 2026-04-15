package com.gresk.modules.account.infrastructure.web;

import com.gresk.modules.account.application.dto.AccountAdminSummary;
import com.gresk.modules.account.application.usecase.*;
import com.gresk.shared.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ActivateAccountUseCase activateAccountUseCase;
    private final ApprovePromoterUseCase approvePromoterUseCase;
    private final SuspendAccountUseCase suspendAccountUseCase;
    private final GetAccountsForAdminUseCase getAccountsUseCase;

    @GetMapping("/promoters")
    public ResponseEntity<List<AccountAdminSummary>> getPromoters(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        List<AccountAdminSummary> result = getAccountsUseCase.execute(Role.PROMOTER, city, status, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<AccountAdminSummary>> getUsers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        List<AccountAdminSummary> result = getAccountsUseCase.execute(Role.USER, city, status, pageable);
        return ResponseEntity.ok(result);
    }

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