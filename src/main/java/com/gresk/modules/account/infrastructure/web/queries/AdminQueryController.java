package com.gresk.modules.account.infrastructure.web.queries;

import com.gresk.modules.account.application.handler.GetPromotersForAdminHandler;
import com.gresk.modules.account.application.handler.GetUsersForAdminHandler;
import com.gresk.shared.domain.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminQueryController {

    private final GetUsersForAdminHandler getUsersForAdminHandler;
    private final GetPromotersForAdminHandler getPromotersForAdminHandler;

    @GetMapping("/promoters")
    public ResponseEntity<List<AccountsAdminDTO>> getPromoters(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        List<AccountsAdminDTO> result = getPromotersForAdminHandler.execute(city, status, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<AccountsAdminDTO>> getUsers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        List<AccountsAdminDTO> result = getUsersForAdminHandler.execute(city, status, pageable);
        return ResponseEntity.ok(result);
    }
}
