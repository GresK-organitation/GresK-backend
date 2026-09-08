package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.MarkDocumentExpiryAlertReadPort;
import com.gresk.modules.artist.domain.exception.DocumentExpiryAlertNotFoundException;
import com.gresk.modules.artist.domain.exception.DocumentExpiryAlertNotOwnedException;
import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import com.gresk.modules.artist.domain.model.valueobject.DocumentExpiryAlertId;
import com.gresk.modules.artist.domain.port.out.DocumentExpiryAlertRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkDocumentExpiryAlertReadUseCase implements MarkDocumentExpiryAlertReadPort {

    private final DocumentExpiryAlertRepositoryPort alertRepository;

    @Override
    public DocumentExpiryAlert execute(String alertId, String promoterId) {
        DocumentExpiryAlert alert = alertRepository.findById(DocumentExpiryAlertId.of(alertId))
                .orElseThrow(() -> new DocumentExpiryAlertNotFoundException(alertId));
        if (!alert.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new DocumentExpiryAlertNotOwnedException();
        }
        alert.markRead();
        return alertRepository.save(alert);
    }
}
