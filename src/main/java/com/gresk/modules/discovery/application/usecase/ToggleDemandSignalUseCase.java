package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.DemandSignal;
import com.gresk.modules.discovery.domain.port.out.DemandSignalRepository;
import com.gresk.modules.discovery.domain.port.out.UserProfilePort;
import com.gresk.modules.user.domain.exception.UserNotFoundException;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * "Quiero que vengan a mi ciudad": toggle de una señal de demanda por
 * (artista, usuario). Segunda pulsación retira la señal.
 */
@Service
@RequiredArgsConstructor
public class ToggleDemandSignalUseCase {

    private final DemandSignalRepository demandSignalRepository;
    private final UserProfilePort userProfilePort;

    @Transactional
    public boolean execute(ArtistId artistId, UserId userId) {
        var existing = demandSignalRepository.findByArtistAndUser(artistId, userId);
        if (existing.isPresent()) {
            demandSignalRepository.delete(existing.get());
            return false;
        }

        String city = userProfilePort.findCityByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        demandSignalRepository.save(DemandSignal.create(artistId, userId, city));
        return true;
    }
}
