package com.gresk.modules.logistics.application.command;

import java.util.List;

public record UpdateTravelPartyMembersCommand(String travelPartyId, String promoterId,
                                               List<TravelPartyMemberInput> members) {
}
