package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.dto.PromoterDashboardDTO;
import com.gresk.modules.promoter.application.port.in.GetPromoterDashboardPort;
import com.gresk.modules.promoter.domain.exception.PromoterNotActiveException;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterStats;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import com.gresk.modules.promoter.domain.port.out.PromoterStatsProviderPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.port.out.AccountStatusPort;
import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPromoterDashboardUseCase implements GetPromoterDashboardPort {

    private final PromoterRepositoryPort promoterRepository;
    private final PromoterStatsProviderPort statsProvider;
    private final ImageUrlResolverPort imageUrlResolver;
    private final AccountStatusPort accountStatusPort;

    @Override
    public PromoterDashboardDTO execute(UUID accountId) {
        if (accountStatusPort.getStatus(accountId) != AccountStatus.ACTIVE) {
            throw new PromoterNotActiveException("Promoter account is not active: " + accountId);
        }

        PromoterId id = PromoterId.of(accountId.toString());

        Promoter promoter = promoterRepository.findById(id)
                .orElseThrow(() -> new PromoterNotFoundException(id.value().toString()));

        PromoterStats stats = statsProvider.getStatsByPromoterId(id);

        return new PromoterDashboardDTO(
                promoter.getName().value(),
                imageUrlResolver.resolveOrDefault(promoter.getLogoAssetId()),
                promoter.getDescription().value(),
                promoter.getAddress().street(),
                promoter.getAddress().city().value(),
                promoter.getAddress().country(),
                promoter.getMusicalGenres().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()),
                stats.totalRevenue(),
                stats.totalEvents(),
                stats.averageRating(),
                stats.totalAttendees(),
                stats.sellThrough(),
                stats.activeEvents(),
                stats.pendingEvents(),
                stats.avgTicketPrice()
        );
    }
}
