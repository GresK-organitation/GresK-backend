package com.gresk.modules.musicdna.application.port.out;

import com.gresk.modules.user.domain.model.UserId;

/**
 * Dispara el recálculo del ADN Musical de forma asíncrona. Implementado en
 * infraestructura con {@code @Async} — la capa de aplicación solo conoce
 * este puerto, nunca el mecanismo de asincronía (evita el problema clásico
 * de Spring AOP ignorando {@code @Async} en auto-invocación).
 */
public interface MusicDnaAsyncRecalculationPort {
    void recalculateAsync(UserId userId);
}
