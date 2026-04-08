package com.gresk.shared.domain.port.out;

import com.gresk.shared.domain.valueobject.AssetId;

public interface ImageUrlResolverPort {
    String resolveOrDefault(AssetId assetId);
}

