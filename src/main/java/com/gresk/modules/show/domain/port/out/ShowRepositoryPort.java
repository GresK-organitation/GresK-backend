package com.gresk.modules.show.domain.port.out;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowId;
import com.gresk.modules.show.domain.model.ShowStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ShowRepositoryPort {
    Show save(Show show);
    Optional<Show> findById(ShowId id);
    List<Show> findByPromoter(PromoterId promoterId);
    List<Show> findByPromoterAndStatus(PromoterId promoterId, ShowStatus status);

    /** Holds cuya ventana ha caducado y siguen en OPCION_HOLD; usado por el scheduler de expiración. */
    List<Show> findExpirableHolds(Instant now);
}
