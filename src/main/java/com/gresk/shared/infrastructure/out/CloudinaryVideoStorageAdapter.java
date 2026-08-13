package com.gresk.shared.infrastructure.out;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gresk.shared.domain.exception.VideoStorageException;
import com.gresk.shared.domain.port.out.VideoStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.VideoUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Uploads short video clips (max {@value #MAX_DURATION_SECONDS}s) to Cloudinary.
 * Requests a server-side eager trim so the stored/served asset never exceeds
 * the cap even if the client didn't trim it — if the account/plan doesn't
 * honour the eager transform, the untrimmed duration is used instead and the
 * upload is rejected rather than silently accepting an overlong clip.
 */
@Component
@RequiredArgsConstructor
public class CloudinaryVideoStorageAdapter implements VideoStoragePort {

    private static final int MAX_DURATION_SECONDS = 15;

    private final Cloudinary cloudinary;

    @Override
    public VideoUploadResult upload(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new VideoStorageException("Cannot upload empty file");
        }

        Map<?, ?> result;
        try {
            result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "video",
                    "eager", List.of(ObjectUtils.asMap(
                            "duration", MAX_DURATION_SECONDS,
                            "crop", "limit")),
                    "eager_async", false,
                    "overwrite", true,
                    "invalidate", true
            ));
        } catch (IOException e) {
            throw new VideoStorageException("Failed to upload video to Cloudinary", e);
        }

        String publicId = (String) result.get("public_id");
        int durationSeconds = extractDuration(result);

        if (durationSeconds > MAX_DURATION_SECONDS) {
            delete(AssetId.of(publicId));
            throw new VideoStorageException(
                    "Video exceeds the maximum allowed duration of " + MAX_DURATION_SECONDS + " seconds");
        }

        return new VideoUploadResult(AssetId.of(publicId), durationSeconds);
    }

    @Override
    public void delete(AssetId assetId) {
        if (assetId == null || assetId.isEmpty()) return;
        try {
            cloudinary.uploader().destroy(assetId.value(), ObjectUtils.asMap("resource_type", "video"));
        } catch (IOException e) {
            throw new VideoStorageException("Failed to delete video from Cloudinary", e);
        }
    }

    @SuppressWarnings("unchecked")
    private int extractDuration(Map<?, ?> result) {
        Object eager = result.get("eager");
        if (eager instanceof List<?> eagerList && !eagerList.isEmpty()
                && eagerList.get(0) instanceof Map<?, ?> firstEager
                && firstEager.get("duration") instanceof Number eagerDuration) {
            return (int) Math.round(eagerDuration.doubleValue());
        }
        if (result.get("duration") instanceof Number rawDuration) {
            return (int) Math.round(rawDuration.doubleValue());
        }
        throw new VideoStorageException("Cloudinary response did not include a video duration");
    }
}
