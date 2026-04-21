package com.gresk.shared.infrastructure.out;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gresk.shared.domain.exception.ImageStorageException;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloudinaryImageStorageAdapter implements ImageStoragePort {

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
                            "folder", folder,
                            "resource_type", "image",
                            "overwrite", true,
                            "invalidate", true
                    )
            );

            // El public_id ya incluye la carpeta (ej: "folder/archivo")
            String publicId = (String) result.get("public_id");
            return AssetId.of(publicId);
        } catch (IOException e) {
            throw new ImageStorageException("Failed to upload image to Cloudinary", e);
        }
    }

    @Override
    public void delete(AssetId assetId) {
        if (assetId == null || assetId.isEmpty()) return;
        try {
            cloudinary.uploader().destroy(assetId.value(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new ImageStorageException("Failed to delete image from Cloudinary", e);
        }
    }
}
