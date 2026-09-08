package com.gresk.modules.venue.application.port.in;

import com.gresk.modules.venue.domain.model.Venue;

import java.util.List;

public interface ListVenuesUseCase {
    List<Venue> execute(String promoterId);
}
