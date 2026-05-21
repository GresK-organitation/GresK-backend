package com.gresk.modules.account.application.handler;

import com.gresk.modules.account.infrastructure.persistence.jpa.AdminPromoterQueryRepository;
import com.gresk.modules.account.infrastructure.web.queries.AccountsAdminDTO;
import com.gresk.shared.domain.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPromotersForAdminHandler {
    private final AdminPromoterQueryRepository adminPromoterQueryRepository;

    public List<AccountsAdminDTO> execute(String rawCity, String rawStatus, Pageable pageable) {
        AccountStatus status = (rawStatus == null || rawStatus.isBlank())
                ? null
                : AccountStatus.valueOf(rawStatus.toUpperCase().trim());

        String city = (rawCity == null || rawCity.isBlank())
                ? null
                : rawCity.trim();

        return adminPromoterQueryRepository.findForAdmin(status, city, pageable)
                .getContent();
    }
}