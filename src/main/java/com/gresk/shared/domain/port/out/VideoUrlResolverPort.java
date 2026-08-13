package com.gresk.shared.domain.port.out;

import com.gresk.shared.domain.valueobject.AssetId;

public interface VideoUrlResolverPort {
    String resolveOrNull(AssetId assetId);
}
