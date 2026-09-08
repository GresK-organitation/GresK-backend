package com.gresk.modules.agenda.infrastructure.persistence.repository;

import com.gresk.modules.agenda.infrastructure.persistence.entity.CalendarEventMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CalendarEventMappingJpaRepository extends JpaRepository<CalendarEventMappingEntity, UUID> {
}
