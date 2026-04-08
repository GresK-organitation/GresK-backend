package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.port.in.UpdatePromoterLogoPort;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdatePromoterLogoUseCase implements UpdatePromoterLogoPort {

    private final PromoterRepositoryPort promoterRepository;
    private final ImageStoragePort imageStorage;

    @Transactional
    @Override
    public void execute(UUID accountId, MultipartFile file) {
        PromoterId id = PromoterId.of(accountId.toString());

        Promoter promoter = promoterRepository.findById(id)
                .orElseThrow(() -> new PromoterNotFoundException(id.value().toString()));

        AssetId assetId = imageStorage.upload(file, "promoters/logos");
        promoter.updateLogo(assetId);

        promoterRepository.save(promoter);
    }
}
