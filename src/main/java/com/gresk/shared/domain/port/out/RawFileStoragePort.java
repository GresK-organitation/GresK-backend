package com.gresk.shared.domain.port.out;

import com.gresk.shared.domain.valueobject.AssetId;
import org.springframework.web.multipart.MultipartFile;

public interface RawFileStoragePort {
    AssetId upload(MultipartFile file, String folder);
    void    delete(AssetId assetId);
    String  resolveUrl(AssetId assetId);
}
