package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.OpenSalesCommand;
import com.gresk.modules.show.domain.model.Show;

/** Invoca {@code MarketplaceListingPort} para publicar el listado público y enlaza su id. */
public interface OpenSalesUseCase {
    Show execute(OpenSalesCommand command);
}
