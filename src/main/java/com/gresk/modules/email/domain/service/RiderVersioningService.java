package com.gresk.modules.email.domain.service;

import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.model.RiderDiff;
import com.gresk.modules.email.domain.model.RiderDiff.FieldChange;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Versionado de riders detectados por email: calcula el diff campo a campo
 * entre la versión anterior y la entrante, y el siguiente número de versión.
 */
@Component
public class RiderVersioningService {

    public RiderDiff computeDiff(Map<String, Object> previous, Map<String, Object> incoming) {
        Map<String, Object> prev = previous != null ? previous : Map.of();
        Map<String, Object> next = incoming != null ? incoming : Map.of();

        List<String>      added    = new ArrayList<>();
        List<String>      removed  = new ArrayList<>();
        List<FieldChange> modified = new ArrayList<>();

        next.forEach((key, newVal) -> {
            if (!prev.containsKey(key)) {
                added.add(key + ": " + newVal);
            } else {
                Object oldVal = prev.get(key);
                if (!Objects.equals(String.valueOf(oldVal), String.valueOf(newVal))) {
                    modified.add(new FieldChange(key, String.valueOf(oldVal), String.valueOf(newVal)));
                }
            }
        });

        prev.keySet().stream()
                .filter(k -> !next.containsKey(k))
                .forEach(k -> removed.add(k + ": " + prev.get(k)));

        return new RiderDiff(added, removed, modified);
    }

    public int nextVersionNumber(Optional<EmailRiderVersion> latest) {
        return latest.map(v -> v.getVersionNumber() + 1).orElse(1);
    }
}
