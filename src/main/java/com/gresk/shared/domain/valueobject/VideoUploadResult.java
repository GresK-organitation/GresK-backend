package com.gresk.shared.domain.valueobject;

/**
 * Result of a video upload: the storage provider reports back the actual
 * (possibly trimmed) duration so the caller never has to trust client input.
 */
public record VideoUploadResult(AssetId assetId, int durationSeconds) {
}
