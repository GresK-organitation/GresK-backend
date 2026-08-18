package com.gresk.modules.tendencias.chronicle.application.usecase;

import com.gresk.modules.tendencias.chronicle.domain.model.Chronicle;
import com.gresk.modules.tendencias.chronicle.domain.port.out.ChronicleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListPublishedChroniclesUseCase {

    private final ChronicleRepositoryPort chronicleRepository;

    public List<Chronicle> execute(int page, int size) {
        return chronicleRepository.findPublished(page, size);
    }
}
