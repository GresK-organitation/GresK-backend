package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.model.valueobject.DocumentExpiryAlertId;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface DocumentExpiryAlertRepositoryPort {
    DocumentExpiryAlert save(DocumentExpiryAlert alert);
    Optional<DocumentExpiryAlert> findById(DocumentExpiryAlertId id);
    List<DocumentExpiryAlert> findAllByPromoterId(PromoterId promoterId);
    /** Evita duplicar alertas: ¿ya existe una alerta sin leer para este documento? */
    boolean existsUnreadFor(BandMemberId bandMemberId, IdentityDocumentType documentType);
}
