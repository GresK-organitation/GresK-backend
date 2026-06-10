package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.port.out.EventLinkerPort;
import com.gresk.modules.email.infrastructure.persistence.entity.EmailMessageEntity;
import com.gresk.modules.email.infrastructure.persistence.repository.EmailMessageJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Vincula correos a eventos por señal de hilo: si otro correo del mismo
 * hilo ya está vinculado a un evento, el nuevo correo pertenece al mismo.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEventLinkerAdapter implements EventLinkerPort {

    private final EmailMessageJpaRepository repo;

    @Override
    public Optional<UUID> findEventForThread(PromoterId promoterId, String threadIdExternal) {
        if (threadIdExternal == null || threadIdExternal.isBlank()) {
            return Optional.empty();
        }
        return repo.findFirstByPromoterIdAndExternalThreadIdAndEventIdIsNotNullOrderByReceivedAtDesc(
                        promoterId.value(), threadIdExternal)
                .map(EmailMessageEntity::getEventId);
    }
}
