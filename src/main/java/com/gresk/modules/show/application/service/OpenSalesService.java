package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.OpenSalesCommand;
import com.gresk.modules.show.application.port.in.OpenSalesUseCase;
import com.gresk.modules.show.domain.exception.IncompleteShowException;
import com.gresk.modules.show.domain.exception.InvalidShowStatusTransitionException;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.model.valueobject.FinancialSimulation;
import com.gresk.modules.show.domain.model.valueobject.VenueBooking;
import com.gresk.modules.show.domain.port.out.MarketplaceListingPort;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OpenSalesService implements OpenSalesUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;
    private final MarketplaceListingPort marketplaceListingPort;

    @Override
    public Show execute(OpenSalesCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());
        ShowStatus from = show.getStatus();

        if (!from.canTransitionTo(ShowStatus.EN_VENTA)) {
            throw new InvalidShowStatusTransitionException(from, ShowStatus.EN_VENTA);
        }
        VenueBooking venueBooking = show.getVenueBooking();
        FinancialSimulation simulation = show.getFinancialSimulation();
        if (venueBooking == null || simulation == null) {
            throw new IncompleteShowException("Cannot open sales: venue/capacity or viability simulation is missing");
        }

        UUID marketplaceEventId = marketplaceListingPort.publishListing(new MarketplaceListingPort.PublishListingRequest(
                show.getId().value(), command.promoterId(), show.getName(), command.genre().name(),
                show.getScheduledDate(), venueBooking.confirmedCapacity(),
                simulation.revenueAssumptions().avgTicketPrice(), simulation.revenueAssumptions().currency()));

        show.openSales(marketplaceEventId);
        Show saved = showRepository.save(show);

        showLogRepository.save(ShowLogEntry.statusChange(saved.getId(), command.promoterId(), from, saved.getStatus()));
        return saved;
    }
}
