package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.dto.PromoterProfileDTO;
import com.gresk.modules.promoter.application.port.in.GetPromoterByAccountIdPort;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPromoterByAccountIdUseCase implements GetPromoterByAccountIdPort {

    private final PromoterRepositoryPort promoterRepository;
    private final ImageUrlResolverPort imageUrlResolver;

    @Override
    public PromoterProfileDTO execute(UUID accountId) {
        PromoterId id = PromoterId.of(accountId.toString());

        Promoter promoter = promoterRepository.findById(id)
                .orElseThrow(() -> new PromoterNotFoundException(accountId.toString()));

        String logoUrl = imageUrlResolver.resolveOrDefault(promoter.getLogoAssetId());

        return new PromoterProfileDTO(
                promoter.getId().value().toString(),
                promoter.getName().value(),
                promoter.getEmail().value(),
                promoter.getAddress().street(),
                promoter.getAddress().city().value(),
                promoter.getAddress().country(),
                logoUrl,
                promoter.getDescription().value(),
                promoter.getStatus().name(),
                promoter.getMusicalGenres().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()),
                promoter.getCreatedAt().toString()
        );
    }
}
