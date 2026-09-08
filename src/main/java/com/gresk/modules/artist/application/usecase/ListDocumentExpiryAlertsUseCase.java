package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.ListDocumentExpiryAlertsPort;
import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import com.gresk.modules.artist.domain.port.out.DocumentExpiryAlertRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListDocumentExpiryAlertsUseCase implements ListDocumentExpiryAlertsPort {

    private final DocumentExpiryAlertRepositoryPort alertRepository;

    @Override
    public List<DocumentExpiryAlert> execute(String promoterId) {
        return alertRepository.findAllByPromoterId(PromoterId.of(promoterId));
    }
}
