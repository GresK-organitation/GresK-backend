package com.gresk.modules.venue.application.port.in;

import com.gresk.modules.venue.application.command.UpdateMunicipalLicenseCommand;
import com.gresk.modules.venue.domain.model.Venue;

public interface UpdateMunicipalLicenseUseCase {
    Venue execute(UpdateMunicipalLicenseCommand command);
}
