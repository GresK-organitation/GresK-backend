package com.gresk.modules.account.application.usecase;

import com.gresk.modules.account.application.dto.AccountAdminSummary;
import com.gresk.modules.account.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAccountsForAdminUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    public List<AccountAdminSummary> execute(Role role, String city, String status) {
        AccountStatus accountStatus = (status == null || status.isBlank())
                ? null
                : AccountStatus.valueOf(status.toUpperCase());
        City cityFilter = (city != null && !city.isBlank()) ? City.of(city.trim()) : null;
        return switch (role) {
            case USER -> accountRepositoryPort.findUsersForAdmin(accountStatus, cityFilter).stream()
                    .map(user -> AccountAdminSummary.builder()
                            .id(user.getId().value())
                            .name(user.getName().value())
                            .email(user.getEmail().value())
                            .city(user.getCity().value())
                            .status(accountStatus)
                            .createdAt(user.getCreatedAt())
                            .build())
                    .toList();

            case PROMOTER -> accountRepositoryPort.findPromotersForAdmin(accountStatus, cityFilter).stream()
                    .map(promoter -> AccountAdminSummary.builder()
                            .id(promoter.getId().value())
                            .name(promoter.getName().value())
                            .email(promoter.getEmail().value())
                            .city(promoter.getAddress().city().value())
                            .status(accountStatus)
                            .createdAt(promoter.getCreatedAt())
                            .build())
                    .toList();
            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        };
    }
}