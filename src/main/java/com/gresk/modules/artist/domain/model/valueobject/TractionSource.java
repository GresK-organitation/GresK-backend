package com.gresk.modules.artist.domain.model.valueobject;

/** Fuente de un dato de tracción geográfica/audiencia. Permite mezclar fuentes
 *  de fiabilidad y cadencia distinta sin perder trazabilidad de origen. */
public enum TractionSource {
    SPOTIFY_FOR_ARTISTS,
    BANDSINTOWN,
    CHARTMETRIC,
    MANUAL
}
