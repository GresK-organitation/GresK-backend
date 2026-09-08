package com.gresk.modules.logistics.infrastructure.persistence.mapper;

import com.gresk.modules.logistics.domain.model.NeedType;
import com.gresk.modules.logistics.domain.model.PersonType;
import com.gresk.modules.logistics.domain.model.RoomType;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.model.TravelPartyId;
import com.gresk.modules.logistics.domain.model.TravelRole;
import com.gresk.modules.logistics.domain.model.valueobject.IndividualNeed;
import com.gresk.modules.logistics.domain.model.valueobject.PersonRef;
import com.gresk.modules.logistics.domain.model.valueobject.RoomingPreference;
import com.gresk.modules.logistics.domain.model.valueobject.TravelPartyMember;
import com.gresk.modules.logistics.infrastructure.persistence.entity.IndividualNeedEmbeddable;
import com.gresk.modules.logistics.infrastructure.persistence.entity.TravelPartyEntity;
import com.gresk.modules.logistics.infrastructure.persistence.entity.TravelPartyMemberEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class TravelPartyMapper {

    public TravelParty toDomain(TravelPartyEntity entity) {
        List<TravelPartyMember> members = entity.getMembers().stream().map(this::toDomain).toList();
        return TravelParty.reconstitute(TravelPartyId.of(entity.getId()), TourId.of(entity.getTourId()),
                PromoterId.of(entity.getPromoterId()), entity.getCreatedAt(), members, entity.getUpdatedAt());
    }

    private TravelPartyMember toDomain(TravelPartyMemberEntity e) {
        PersonRef personRef = new PersonRef(PersonType.valueOf(e.getPersonType()), e.getPersonId());
        List<IndividualNeed> needs = e.getNeeds().stream()
                .map(n -> new IndividualNeed(NeedType.valueOf(n.getType()), n.getDescription())).toList();
        RoomingPreference preference = new RoomingPreference(
                e.getPreferredRoomType() == null ? null : RoomType.valueOf(e.getPreferredRoomType()),
                e.getPreferredRoommateId(), List.copyOf(e.getDoNotShareWith()));
        return new TravelPartyMember(e.getId(), personRef, e.getDisplayName(), TravelRole.valueOf(e.getRole()),
                needs, preference, e.isActive());
    }

    public TravelPartyEntity toEntity(TravelParty travelParty) {
        TravelPartyEntity entity = TravelPartyEntity.builder()
                .id(travelParty.getId().value())
                .tourId(travelParty.getTourId().value())
                .promoterId(travelParty.getPromoterId().value())
                .createdAt(travelParty.getCreatedAt())
                .updatedAt(travelParty.getUpdatedAt())
                .build();

        List<TravelPartyMemberEntity> members = new ArrayList<>();
        for (TravelPartyMember m : travelParty.getMembers()) {
            members.add(toEntity(m, entity));
        }
        entity.setMembers(members);
        return entity;
    }

    private TravelPartyMemberEntity toEntity(TravelPartyMember m, TravelPartyEntity parent) {
        List<IndividualNeedEmbeddable> needs = new ArrayList<>();
        for (IndividualNeed n : m.needs()) {
            needs.add(IndividualNeedEmbeddable.builder().type(n.type().name()).description(n.description()).build());
        }
        RoomingPreference preference = m.roomingPreference();
        return TravelPartyMemberEntity.builder()
                .id(m.id())
                .travelParty(parent)
                .personType(m.personRef().type().name())
                .personId(m.personRef().personId())
                .displayName(m.displayName())
                .role(m.role().name())
                .active(m.active())
                .preferredRoomType(preference.preferredRoomType() == null ? null : preference.preferredRoomType().name())
                .preferredRoommateId(preference.preferredRoommateId())
                .doNotShareWith(new HashSet<>(preference.doNotShareWith()))
                .needs(needs)
                .build();
    }
}
