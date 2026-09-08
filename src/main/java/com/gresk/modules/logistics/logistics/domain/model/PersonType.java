package com.gresk.modules.logistics.domain.model;

/**
 * Origen de una persona referenciada en un TravelParty: un músico ya dado de alta
 * como artist.BandMember, o un crew propio del módulo logistics (tour manager,
 * técnicos, conductores...) sin vínculo a ningún Artist.
 */
public enum PersonType {
    BAND_MEMBER,
    CREW
}
