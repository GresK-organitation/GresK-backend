package com.gresk.modules.logistics.application.dto;

import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.model.valueobject.IndividualNeed;
import com.gresk.modules.logistics.domain.model.valueobject.RoomingPreference;
import com.gresk.modules.logistics.domain.model.valueobject.TravelPartyMember;
import org.springframework.stereotype.Component;

@Component
public class TravelPartyResponseMapper {

    public TravelPartyResponse toResponse(TravelParty travelParty) {
        return new TravelPartyResponse(
                travelParty.getId().toString(),
                travelParty.getTourId().toString(),
                travelParty.getMembers().stream().map(this::toMemberView).toList(),
                travelParty.getUpdatedAt());
    }

    private TravelPartyResponse.MemberView toMemberView(TravelPartyMember m) {
        return new TravelPartyResponse.MemberView(
                m.id().toString(),
                m.personRef().type().name(),
                m.personRef().personId().toString(),
                m.displayName(),
                m.role().name(),
                m.needs().stream().map(this::toNeedView).toList(),
                toRoomingPreferenceView(m.roomingPreference()),
                m.active());
    }

    private TravelPartyResponse.NeedView toNeedView(IndividualNeed n) {
        return new TravelPartyResponse.NeedView(n.type().name(), n.description());
    }

    private TravelPartyResponse.RoomingPreferenceView toRoomingPreferenceView(RoomingPreference p) {
        return new TravelPartyResponse.RoomingPreferenceView(
                p.preferredRoomType() == null ? null : p.preferredRoomType().name(),
                p.preferredRoommateId() == null ? null : p.preferredRoommateId().toString(),
                p.doNotShareWith().stream().map(Object::toString).toList());
    }
}
