package com.gresk.modules.tendencias.chronicle.infrastructure.rss;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/** Quita el HTML del {@code <description>}/{@code <summary>} crudo del feed, sin nueva dependencia de parsing. */
@Component
public class FeedSummarySanitizer {

    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    public String stripHtml(String raw) {
        if (raw == null) return "";
        String noTags = TAG_PATTERN.matcher(raw).replaceAll(" ");
        String unescaped = HtmlUtils.htmlUnescape(noTags);
        return WHITESPACE_PATTERN.matcher(unescaped).replaceAll(" ").trim();
    }
}
