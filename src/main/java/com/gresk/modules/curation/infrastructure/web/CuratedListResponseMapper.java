package com.gresk.modules.curation.infrastructure.web;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListItem;
import org.springframework.stereotype.Component;

@Component
public class CuratedListResponseMapper {

    public CuratedListResponse toResponse(CuratedList list) {
        return new CuratedListResponse(
                list.getId().toString(),
                list.getOwnerId().toString(),
                list.getTitle(),
                list.getDescription(),
                list.getVisibility().name(),
                list.getItems().stream().map(this::toResponse).toList(),
                list.getCreatedAt().toString(),
                list.getUpdatedAt().toString()
        );
    }

    private CuratedListItemResponse toResponse(CuratedListItem item) {
        return new CuratedListItemResponse(
                item.id().toString(),
                item.entryType().name(),
                item.entryId().toString(),
                item.position(),
                item.addedAt().toString()
        );
    }
}
