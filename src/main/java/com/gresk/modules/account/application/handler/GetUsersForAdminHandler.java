package com.gresk.modules.account.application.handler;

import com.gresk.modules.account.infrastructure.persistence.jpa.AdminUserQueryRepository;
import com.gresk.modules.account.infrastructure.web.queries.AccountsAdminDTO;
import com.gresk.shared.domain.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUsersForAdminHandler {
    private final AdminUserQueryRepository adminUserQueryRepository;

    public List<AccountsAdminDTO> execute(String rawCity, String rawStatus, Pageable pageable) {
        AccountStatus status = (rawStatus == null || rawStatus.isBlank())
                ? null
                : AccountStatus.valueOf(rawStatus.toUpperCase().trim());

        String city = (rawCity == null || rawCity.isBlank())
                ? null
                : rawCity.trim();

        return adminUserQueryRepository.findForAdmin(status, city, pageable)
                .getContent();
    }
}
