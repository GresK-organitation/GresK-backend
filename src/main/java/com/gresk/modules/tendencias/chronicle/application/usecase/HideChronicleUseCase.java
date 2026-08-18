package com.gresk.modules.tendencias.chronicle.application.usecase;

import com.gresk.modules.tendencias.chronicle.domain.exception.ChronicleNotFoundException;
import com.gresk.modules.tendencias.chronicle.domain.model.Chronicle;
import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleId;
import com.gresk.modules.tendencias.chronicle.domain.port.out.ChronicleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HideChronicleUseCase {

    private final ChronicleRepositoryPort chronicleRepository;

    public void execute(ChronicleId id) {
        Chronicle chronicle = chronicleRepository.findById(id)
                .orElseThrow(() -> new ChronicleNotFoundException("Chronicle not found: " + id));
        chronicle.hide();
        chronicleRepository.save(chronicle);
    }
}
