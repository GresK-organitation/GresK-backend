package com.gresk.modules.curation.infrastructure.persistence.mapper;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.model.CuratedListItem;
import com.gresk.modules.curation.domain.model.CuratedListItemId;
import com.gresk.modules.curation.infrastructure.persistence.entity.CuratedListEntity;
import com.gresk.modules.curation.infrastructure.persistence.entity.CuratedListItemEntity;
import com.gresk.modules.user.domain.model.UserId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class CuratedListMapper {

    public CuratedList toDomain(CuratedListEntity e) {
        List<CuratedListItem> items = e.getItems().stream()
                .sorted(Comparator.comparingInt(CuratedListItemEntity::getPosition))
                .map(this::toDomainItem)
                .toList();

        return CuratedList.reconstitute(
                CuratedListId.of(e.getId()),
                UserId.of(e.getOwnerId()),
                e.getCreatedAt(),
                e.getTitle(),
                e.getDescription(),
                e.getVisibility(),
                items,
                e.getUpdatedAt()
        );
    }

    public CuratedListEntity toEntity(CuratedList list) {
        CuratedListEntity entity = CuratedListEntity.builder()
                .id(list.getId().value())
                .ownerId(list.getOwnerId().value())
                .title(list.getTitle())
                .description(list.getDescription())
                .visibility(list.getVisibility())
                .createdAt(list.getCreatedAt())
                .updatedAt(list.getUpdatedAt())
                .build();

        List<CuratedListItemEntity> itemEntities = new ArrayList<>();
        for (CuratedListItem item : list.getItems()) {
            itemEntities.add(toEntityItem(item, entity));
        }
        entity.setItems(itemEntities);

        return entity;
    }

    private CuratedListItem toDomainItem(CuratedListItemEntity e) {
        return new CuratedListItem(
                CuratedListItemId.of(e.getId()),
                e.getEntryType(),
                e.getEntryId(),
                e.getPosition(),
                e.getAddedAt()
        );
    }

    private CuratedListItemEntity toEntityItem(CuratedListItem item, CuratedListEntity list) {
        return CuratedListItemEntity.builder()
                .id(item.id().value())
                .list(list)
                .entryType(item.entryType())
                .entryId(item.entryId())
                .position(item.position())
                .addedAt(item.addedAt())
                .build();
    }
}
