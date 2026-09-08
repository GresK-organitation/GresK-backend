package com.gresk.modules.agenda.domain.model;

/** Tipo local sincronizado con el calendario externo. AGENDA_ENTRY es bidireccional;
 *  BOOKING es solo push (un cambio externo nunca debe mutar la máquina de estados de un Booking). */
public enum LocalEntryType {
    AGENDA_ENTRY,
    BOOKING
}
