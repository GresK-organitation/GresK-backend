package com.gresk.modules.account.infrastructure.web.queries;

import com.gresk.modules.account.infrastructure.persistence.jpa.AdminPromoterQueryRepository;
import com.gresk.modules.account.infrastructure.persistence.jpa.AdminUserQueryRepository;
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

    private final AdminUserQueryRepository userQueryRepository;
    private final AdminPromoterQueryRepository promoterQueryRepository;

    @GetMapping("/promoters")
    public ResponseEntity<List<AccountsAdminDTO>> getPromoters(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        AccountStatus accountStatus = parseStatus(status);
        String cityFilter = parseCity(city);

        List<AccountsAdminDTO> result = promoterQueryRepository
                .findForAdmin(accountStatus, cityFilter, pageable)
                .getContent();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<AccountsAdminDTO>> getUsers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        AccountStatus accountStatus = parseStatus(status);
        String cityFilter = parseCity(city);

        List<AccountsAdminDTO> result = userQueryRepository
                .findForAdmin(accountStatus, cityFilter, pageable)
                .getContent();

        return ResponseEntity.ok(result);
    }

    private AccountStatus parseStatus(String status) {
        return (status == null || status.isBlank())
                ? null
                : AccountStatus.valueOf(status.toUpperCase().trim());
    }

    private String parseCity(String city) {
        return (city != null && !city.isBlank()) ? city.trim() : null;
    }
}
