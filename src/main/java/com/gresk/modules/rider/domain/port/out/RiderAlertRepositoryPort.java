package com.gresk.modules.rider.domain.port.out;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.AlertId;
import com.gresk.modules.rider.domain.model.RiderAlert;

import java.util.List;
import java.util.Optional;

public interface RiderAlertRepositoryPort {

    RiderAlert save(RiderAlert alert);

    Optional<RiderAlert> findById(AlertId id);

    List<RiderAlert> findUnreadByPromoterId(PromoterId promoterId);

    long countUnreadByPromoterId(PromoterId promoterId);
}
