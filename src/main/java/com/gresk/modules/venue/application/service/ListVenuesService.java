package com.gresk.modules.venue.application.service;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.venue.application.port.in.ListVenuesUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListVenuesService implements ListVenuesUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public List<Venue> execute(String promoterId) {
        return venueRepository.findByOwner(PromoterId.of(promoterId));
    }
}
