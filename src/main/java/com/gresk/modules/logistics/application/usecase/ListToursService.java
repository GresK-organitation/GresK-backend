package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.ListToursUseCase;
import com.gresk.modules.logistics.application.query.ListToursQuery;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.TourStatus;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListToursService implements ListToursUseCase {

    private final TourRepositoryPort tourRepository;

    @Override
    public List<Tour> execute(ListToursQuery query) {
        TourStatus status = query.status() == null ? null : TourStatus.valueOf(query.status());
        return tourRepository.findByPromoterId(PromoterId.of(query.promoterId()), status);
    }
}
