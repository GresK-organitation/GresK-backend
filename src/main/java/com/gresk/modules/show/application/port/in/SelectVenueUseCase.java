package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.SelectVenueCommand;
import com.gresk.modules.show.domain.model.Show;

/**
 * Valida contra {@code VenueTechnicalFileQueryPort} que la configuración de aforo exista y que
 * el venue esté activo antes de fijar el {@code VenueBooking} snapshot en el show; lanza
 * {@code VenueCapacityExceededException} si en el futuro se permite un aforo custom mayor
 * que el máximo configurado en el venue.
 */
public interface SelectVenueUseCase {
    Show execute(SelectVenueCommand command);
}
