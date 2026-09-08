package com.gresk.modules.show.domain.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Ciclo de vida formal de un {@code Show} (la producción de un evento, distinta del listado
 * de venta público modelado en el módulo {@code event}). Se implementa como enum con grafo de
 * transiciones explícito -- el mismo patrón ya usado en {@code EventStatus} y
 * {@code BookingStatus} -- en vez de adoptar Spring StateMachine: a este tamaño de máquina
 * (8 estados, sin sub-estados ni fork/join) una librería de state machine añade una capa de
 * configuración, persistencia de contexto y aprendizaje sin aportar nada que el enum no
 * resuelva ya de forma más simple y 100% testable sin contexto de Spring.
 *
 * <pre>
 * BORRADOR ──► OPCION_HOLD ──► CONFIRMADO ──► EN_VENTA ──► EN_EJECUCION ──► FINALIZADO ──► LIQUIDADO
 *    │              │               │                                                         ▲
 *    └──────────────┴───────────────┴─────────────────────► CANCELADO ◄─────────────────────---┘ (no permitido tras LIQUIDADO)
 * </pre>
 */
public enum ShowStatus {
    BORRADOR,
    OPCION_HOLD,
    CONFIRMADO,
    EN_VENTA,
    EN_EJECUCION,
    FINALIZADO,
    LIQUIDADO,
    CANCELADO;

    private static final Set<ShowStatus> BORRADOR_TARGETS     = EnumSet.of(OPCION_HOLD, CONFIRMADO, CANCELADO);
    private static final Set<ShowStatus> OPCION_HOLD_TARGETS  = EnumSet.of(BORRADOR, CONFIRMADO, CANCELADO);
    private static final Set<ShowStatus> CONFIRMADO_TARGETS   = EnumSet.of(EN_VENTA, CANCELADO);
    private static final Set<ShowStatus> EN_VENTA_TARGETS     = EnumSet.of(EN_EJECUCION, CANCELADO);
    private static final Set<ShowStatus> EN_EJECUCION_TARGETS = EnumSet.of(FINALIZADO);
    private static final Set<ShowStatus> FINALIZADO_TARGETS   = EnumSet.of(LIQUIDADO);

    public boolean canTransitionTo(ShowStatus target) {
        return switch (this) {
            case BORRADOR     -> BORRADOR_TARGETS.contains(target);
            case OPCION_HOLD  -> OPCION_HOLD_TARGETS.contains(target);
            case CONFIRMADO   -> CONFIRMADO_TARGETS.contains(target);
            case EN_VENTA     -> EN_VENTA_TARGETS.contains(target);
            case EN_EJECUCION -> EN_EJECUCION_TARGETS.contains(target);
            case FINALIZADO   -> FINALIZADO_TARGETS.contains(target);
            case LIQUIDADO, CANCELADO -> false;
        };
    }

    public boolean isTerminal() {
        return this == LIQUIDADO || this == CANCELADO;
    }

    /** Estados en los que aún se puede editar libremente la ficha del show. */
    public boolean isEditable() {
        return this == BORRADOR || this == OPCION_HOLD;
    }
}
