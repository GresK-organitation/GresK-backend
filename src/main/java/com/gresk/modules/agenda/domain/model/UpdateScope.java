package com.gresk.modules.agenda.domain.model;

public enum UpdateScope {
    /** Solo modifica esta ocurrencia concreta de la serie. */
    THIS_ONLY,
    /** Modifica esta ocurrencia y todas las siguientes. */
    THIS_AND_FOLLOWING,
    /** Modifica toda la serie. */
    ALL
}
