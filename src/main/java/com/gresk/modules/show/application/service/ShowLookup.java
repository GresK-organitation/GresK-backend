package com.gresk.modules.show.application.service;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.show.domain.exception.ShowNotFoundException;
import com.gresk.modules.show.domain.exception.ShowNotOwnedException;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowId;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;

/** Helper interno: evita repetir el par find-or-404 + check-ownership-or-403 en cada servicio. */
final class ShowLookup {

    private ShowLookup() {
    }

    static Show findOwned(ShowRepositoryPort repository, String showId, String promoterId) {
        Show show = repository.findById(ShowId.of(showId))
                .orElseThrow(() -> new ShowNotFoundException(showId));
        if (!show.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ShowNotOwnedException();
        }
        return show;
    }
}
