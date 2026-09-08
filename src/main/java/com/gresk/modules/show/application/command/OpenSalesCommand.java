package com.gresk.modules.show.application.command;

import com.gresk.shared.domain.MusicGenre;

/**
 * {@code genre} es exclusivo del listado público (el módulo {@code event} lo exige para poder
 * publicar) y no forma parte de la ficha de producción del show, así que se decide en el
 * momento de abrir venta y no antes.
 */
public record OpenSalesCommand(String showId, String promoterId, MusicGenre genre) {
}
