package com.gresk.modules.curation.domain.port.out;

import java.util.UUID;

/**
 * Read-only existence check into the review module — used only to validate
 * a VERIFIED_REVIEW item before it's added to a list. Never reads/writes
 * review content, likes, or the official rating aggregates.
 */
public interface ReviewLookupPort {
    boolean existsById(UUID reviewId);
}
