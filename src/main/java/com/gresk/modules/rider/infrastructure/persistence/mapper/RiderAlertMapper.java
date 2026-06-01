package com.gresk.modules.rider.infrastructure.persistence.mapper;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.AlertId;
import com.gresk.modules.rider.domain.model.RiderAlert;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.infrastructure.persistence.entity.RiderAlertEntity;
import org.springframework.stereotype.Component;

@Component
public class RiderAlertMapper {

    public RiderAlert toDomain(RiderAlertEntity e) {
        return RiderAlert.reconstitute(
                AlertId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                EventId.of(e.getEventId().toString()),
                RiderId.of(e.getRiderId()),
                e.getMessage(),
                e.isRead(),
                e.getCreatedAt()
        );
    }

    public RiderAlertEntity toEntity(RiderAlert alert) {
        return RiderAlertEntity.builder()
                .id(alert.getId().value())
                .promoterId(alert.getPromoterId().value())
                .eventId(alert.getEventId().value())
                .riderId(alert.getRiderId().value())
                .message(alert.getMessage())
                .read(alert.isRead())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
