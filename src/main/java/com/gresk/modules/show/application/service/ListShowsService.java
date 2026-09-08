package com.gresk.modules.show.application.service;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.show.application.port.in.ListShowsUseCase;
import com.gresk.modules.show.application.query.ListShowsQuery;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListShowsService implements ListShowsUseCase {

    private final ShowRepositoryPort showRepository;

    @Override
    public List<Show> execute(ListShowsQuery query) {
        PromoterId promoterId = PromoterId.of(query.promoterId());
        if (query.status() != null) {
            return showRepository.findByPromoterAndStatus(promoterId, query.status());
        }
        return showRepository.findByPromoter(promoterId);
    }
}
