package com.gresk.modules.finance.domain.model.valueobject;

public enum SettlementDealType {
    /** Caché fijo, sin relación con la taquilla. */
    FLAT_FEE,
    /** El artista recibe el MAYOR entre un mínimo garantizado y un % de la taquilla neta. */
    VERSUS,
    /** Reparto directo de taquilla neta (ej. 70/30), sin garantía mínima. */
    DOOR_SPLIT,
    /** El artista recibe el mínimo garantizado MÁS un % sobre la taquilla que exceda un umbral. */
    GUARANTEED_MIN_PLUS_PERCENTAGE
}
