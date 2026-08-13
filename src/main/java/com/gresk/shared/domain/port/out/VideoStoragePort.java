package com.gresk.shared.domain.port.out;

import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.VideoUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface VideoStoragePort {
    VideoUploadResult upload(MultipartFile file, String folder);
    void delete(AssetId assetId);
}
