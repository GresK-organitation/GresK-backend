package com.gresk.modules.logistics.application.command;

import java.util.List;

/**
 * id es nullable: omitido al dar de alta a alguien nuevo, obligatorio para conservar
 * la identidad de un miembro existente al reemplazar el roster completo.
 */
public record TravelPartyMemberInput(String id, String personType, String personId, String displayName, String role,
                                      List<IndividualNeedInput> needs, RoomingPreferenceInput roomingPreference,
                                      Boolean active) {
}
