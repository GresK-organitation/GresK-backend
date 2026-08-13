package com.gresk.modules.curation.domain.port.out;

import com.gresk.modules.curation.domain.model.ListVisibility;
import com.gresk.modules.user.domain.model.UserId;

import java.util.Optional;

public record CuratedListFilter(
        Optional<UserId>         ownerId,
        Optional<ListVisibility> visibility
) {
    public static CuratedListFilter ownedBy(UserId ownerId) {
        return new CuratedListFilter(Optional.of(ownerId), Optional.empty());
    }

    public static CuratedListFilter discoverPublic() {
        return new CuratedListFilter(Optional.empty(), Optional.of(ListVisibility.PUBLIC));
    }
}
