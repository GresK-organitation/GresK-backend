package com.gresk.modules.account.application.usecase;

import com.gresk.modules.account.application.dto.AccountAdminSummary;
import com.gresk.modules.account.infrastructure.persistence.AdminPromoterQueryRepository;
import com.gresk.modules.account.infrastructure.persistence.AdminUserQueryRepository;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAccountsForAdminUseCase {

    private final AdminUserQueryRepository adminUserQueryRepository;
    private final AdminPromoterQueryRepository adminPromoterQueryRepository;

    public List<AccountAdminSummary> execute(Role role, String city, String status, Pageable pageable) {
        AccountStatus accountStatus = (status == null || status.isBlank())
                ? null
                : AccountStatus.valueOf(status.toUpperCase());

        String cityFilter = (city != null && !city.isBlank()) ? city.trim() : null;

        return switch (role) {
            case USER -> adminUserQueryRepository
                    .findForAdmin(accountStatus, cityFilter, pageable)
                    .getContent();
            case PROMOTER -> adminPromoterQueryRepository
                    .findForAdmin(accountStatus, cityFilter, pageable)
                    .getContent();
            default -> throw new IllegalArgumentException("El rol " + role + " no tiene un listado administrativo.");
        };
    }
}