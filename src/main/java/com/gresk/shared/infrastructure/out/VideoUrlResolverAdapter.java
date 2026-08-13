package com.gresk.shared.infrastructure.out;

import com.gresk.shared.domain.port.out.VideoUrlResolverPort;
import com.gresk.shared.domain.valueobject.AssetId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VideoUrlResolverAdapter implements VideoUrlResolverPort {

    private final String assetUrlTemplate;

    public VideoUrlResolverAdapter(@Value("${gresk.videos.asset-url-template}") String assetUrlTemplate) {
        this.assetUrlTemplate = assetUrlTemplate;
    }

    @Override
    public String resolveOrNull(AssetId assetId) {
        if (assetId == null || assetId.isEmpty()) return null;
        return assetUrlTemplate.replace("{assetId}", assetId.value());
    }
}
