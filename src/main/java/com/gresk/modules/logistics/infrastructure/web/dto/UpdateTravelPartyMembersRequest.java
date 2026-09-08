package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.TravelPartyMemberInput;

import java.util.List;

public record UpdateTravelPartyMembersRequest(List<TravelPartyMemberInput> members) {
}
