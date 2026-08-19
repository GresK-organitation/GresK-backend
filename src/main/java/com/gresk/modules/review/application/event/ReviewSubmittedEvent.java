package com.gresk.modules.review.application.event;

import java.util.UUID;

/** Se publica tras persistir una reseña verificada; dispara el recálculo async del ADN Musical. */
public record ReviewSubmittedEvent(UUID userId) {}
