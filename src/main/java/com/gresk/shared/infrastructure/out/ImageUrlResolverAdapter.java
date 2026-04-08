package com.gresk.shared.infrastructure.out;

import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import com.gresk.shared.domain.valueobject.AssetId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlResolverAdapter implements ImageUrlResolverPort {

    private final String defaultUrl;
    private final String assetUrlTemplate;

    public ImageUrlResolverAdapter(
            @Value("${gresk.images.default-url}") String defaultUrl,
            @Value("${gresk.images.asset-url-template}") String assetUrlTemplate
    ) {
        this.defaultUrl = defaultUrl;
        this.assetUrlTemplate = assetUrlTemplate;
    }

    @Override
    public String resolveOrDefault(AssetId assetId) {
        if (assetId == null || assetId.isEmpty()) return defaultUrl;
        return assetUrlTemplate.replace("{assetId}", assetId.value());
    }
}

