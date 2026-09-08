package com.gresk.modules.venue.domain.model.valueobject;

import java.time.LocalTime;

/** Especificaciones de carga/descarga: acceso de vehículos de producción y ventana horaria permitida. */
public record LoadingDockSpec(
        double accessHeightMeters,
        double accessWidthMeters,
        double maxVehicleWeightKg,
        LocalTime windowStart,
        LocalTime windowEnd,
        int dockCount,
        String notes
) {

    public LoadingDockSpec {
        if (accessHeightMeters <= 0 || accessWidthMeters <= 0 || maxVehicleWeightKg <= 0) {
            throw new IllegalArgumentException("LoadingDockSpec dimensions must be positive");
        }
        if (windowStart == null || windowEnd == null || !windowEnd.isAfter(windowStart)) {
            throw new IllegalArgumentException("LoadingDockSpec windowEnd must be after windowStart");
        }
        if (dockCount < 1) {
            throw new IllegalArgumentException("LoadingDockSpec dockCount must be at least 1");
        }
    }
}
