package com.gresk.shared.infrastructure.out;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gresk.shared.domain.exception.ImageStorageException;
import com.gresk.shared.domain.port.out.RawFileStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloudinaryRawFileStorageAdapter implements RawFileStoragePort {

    private final Cloudinary cloudinary;

    @Override
    public AssetId upload(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new ImageStorageException("Cannot upload empty file");
        }
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",        folder,
                            "resource_type", "raw",
                            "overwrite",     true,
                            "invalidate",    true
                    )
            );
            return AssetId.of((String) result.get("public_id"));
        } catch (IOException e) {
            throw new ImageStorageException("Failed to upload raw file to Cloudinary", e);
        }
    }

    @Override
    public void delete(AssetId assetId) {
        if (assetId == null || assetId.isEmpty()) return;
        try {
            cloudinary.uploader().destroy(
                    assetId.value(),
                    ObjectUtils.asMap("resource_type", "raw")
            );
        } catch (IOException e) {
            throw new ImageStorageException("Failed to delete raw file from Cloudinary", e);
        }
    }

    @Override
    public String resolveUrl(AssetId assetId) {
        if (assetId == null || assetId.isEmpty()) return null;
        return cloudinary.url()
                .resourceType("raw")
                .generate(assetId.value());
    }
}
