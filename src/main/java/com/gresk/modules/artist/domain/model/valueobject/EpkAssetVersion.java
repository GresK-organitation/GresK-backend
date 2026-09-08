package com.gresk.modules.artist.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.AssetId;

import java.time.Instant;
import java.util.Objects;

/**
 * Una versión inmutable de un documento del EPK. Vive embebida dentro del
 * aggregate EpkAsset — no tiene repositorio propio porque su invariante
 * (numeración monótona, pertenencia a un único EpkAsset) debe protegerse
 * de forma atómica junto al resto de versiones del mismo documento.
 */
public record EpkAssetVersion(
        int versionNumber,
        AssetId storedFile,
        String fileName,
        String mimeType,
        long fileSizeBytes,
        String uploadedByUserId,
        Instant uploadedAt
) {
    public EpkAssetVersion {
        if (versionNumber < 1) {
            throw new IllegalArgumentException("versionNumber must be >= 1");
        }
        Objects.requireNonNull(storedFile, "storedFile is required");
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is required");
        }
        fileName = fileName.trim();
        if (fileSizeBytes < 0) {
            throw new IllegalArgumentException("fileSizeBytes cannot be negative");
        }
        Objects.requireNonNull(uploadedAt, "uploadedAt is required");
    }

    public static EpkAssetVersion first(AssetId storedFile, String fileName, String mimeType,
                                         long fileSizeBytes, String uploadedByUserId, Instant uploadedAt) {
        return new EpkAssetVersion(1, storedFile, fileName, mimeType, fileSizeBytes, uploadedByUserId, uploadedAt);
    }

    public static EpkAssetVersion of(int versionNumber, AssetId storedFile, String fileName, String mimeType,
                                      long fileSizeBytes, String uploadedByUserId, Instant uploadedAt) {
        return new EpkAssetVersion(versionNumber, storedFile, fileName, mimeType, fileSizeBytes, uploadedByUserId, uploadedAt);
    }
}
