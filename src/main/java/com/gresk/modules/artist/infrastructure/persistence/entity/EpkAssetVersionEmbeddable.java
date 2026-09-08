package com.gresk.modules.artist.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Instant;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpkAssetVersionEmbeddable {

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "stored_file", length = 2048, nullable = false)
    private String storedFile;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "mime_type", length = 150, nullable = false)
    private String mimeType;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "uploaded_by_user_id", length = 100, nullable = false)
    private String uploadedByUserId;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;
}
