package com.gresk.modules.artist.domain.model.valueobject;

import com.gresk.modules.artist.domain.exception.InvalidSocialUrlException;

import java.net.URI;

public record SocialLinks(String instagramUrl, String spotifyUrl) {

    public SocialLinks {
        instagramUrl = sanitizeAndValidate(instagramUrl, "Instagram");
        spotifyUrl   = sanitizeAndValidate(spotifyUrl,   "Spotify");
    }

    public boolean hasInstagram() {
        return instagramUrl != null && !instagramUrl.isBlank();
    }

    public boolean hasSpotify() {
        return spotifyUrl != null && !spotifyUrl.isBlank();
    }

    public static SocialLinks of(String instagram, String spotify) {
        return new SocialLinks(instagram, spotify);
    }

    public static SocialLinks empty() {
        return new SocialLinks(null, null);
    }

    public static SocialLinks reconstitute(String instagram, String spotify) {
        return new SocialLinks(instagram, spotify);
    }

    private static String sanitizeAndValidate(String url, String platform) {
        if (url == null || url.isBlank()) return "";
        url = url.trim();
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new InvalidSocialUrlException(platform + " URL must use http/https: " + url);
            }
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new InvalidSocialUrlException(platform + " URL must include a host: " + url);
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidSocialUrlException(platform + " URL is not a valid URI: " + url);
        }
        return url;
    }
}
