package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.port.out.EmailRiderVersionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Historial de versiones de rider de un evento (la más reciente primero). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetRiderVersionsUseCase {

    private final EmailRiderVersionRepositoryPort repository;

    public List<EmailRiderVersion> execute(UUID eventId) {
        return repository.findByEventId(eventId);
    }
}
