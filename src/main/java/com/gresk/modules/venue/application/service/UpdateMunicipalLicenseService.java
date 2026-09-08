package com.gresk.modules.venue.application.service;

import com.gresk.modules.venue.application.command.UpdateMunicipalLicenseCommand;
import com.gresk.modules.venue.application.port.in.UpdateMunicipalLicenseUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.valueobject.MunicipalLicense;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMunicipalLicenseService implements UpdateMunicipalLicenseUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Venue execute(UpdateMunicipalLicenseCommand command) {
        Venue venue = VenueLookup.findOwned(venueRepository, command.venueId(), command.promoterId());
        venue.registerLicense(new MunicipalLicense(
                command.licenseNumber(), command.type(), command.issuingAuthority(),
                command.validFrom(), command.validUntil()));
        return venueRepository.save(venue);
    }
}
