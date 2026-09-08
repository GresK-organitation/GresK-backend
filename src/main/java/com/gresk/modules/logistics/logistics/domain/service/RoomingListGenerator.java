package com.gresk.modules.logistics.domain.service;

import com.gresk.modules.logistics.domain.exception.InsufficientRoomAllotmentException;
import com.gresk.modules.logistics.domain.model.RoomType;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAllotment;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAssignment;
import com.gresk.modules.logistics.domain.model.valueobject.TravelPartyMember;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Heurística voraz de reparto de habitaciones (no un solver óptimo, similar en espíritu
 * al generador de rooming lists de Master Tour): empareja primero preferencias mutuas de
 * compañero de habitación, asigna individuales a quien prefiere SINGLE, y por último
 * empareja al resto respetando doNotShareWith, todo dentro de los cupos del RoomAllotment.
 * Si el bloque no alcanza para completar el travel party, lanza InsufficientRoomAllotmentException
 * en vez de dejar a alguien sin habitación.
 */
public final class RoomingListGenerator {

    private RoomingListGenerator() {}

    public static List<RoomAssignment> generate(List<TravelPartyMember> members, List<RoomAllotment> allotments) {
        Map<RoomType, Integer> remaining = new EnumMap<>(RoomType.class);
        for (RoomAllotment a : allotments) {
            remaining.merge(a.roomType(), a.quantity(), Integer::sum);
        }

        Map<UUID, TravelPartyMember> byId = new LinkedHashMap<>();
        for (TravelPartyMember m : members) {
            byId.put(m.id(), m);
        }

        List<RoomAssignment> result = new ArrayList<>();
        Set<UUID> placed = new HashSet<>();

        // 1) Parejas mutuas de compañero de habitación (A prefiere a B y B prefiere a A)
        for (TravelPartyMember m : members) {
            if (placed.contains(m.id())) continue;
            UUID preferredId = m.roomingPreference().preferredRoommateId();
            if (preferredId == null || placed.contains(preferredId)) continue;
            TravelPartyMember other = byId.get(preferredId);
            if (other == null) continue;
            boolean mutual = m.id().equals(other.roomingPreference().preferredRoommateId());
            if (!mutual) continue;
            if (m.roomingPreference().doNotShareWith().contains(other.id())) continue;

            RoomType preferred = m.roomingPreference().preferredRoomType();
            RoomType type = (preferred != null && preferred.maxOccupancy() >= 2) ? preferred : RoomType.TWIN;
            type = allocate(remaining, type, RoomType.DOUBLE, RoomType.TWIN);
            result.add(new RoomAssignment(UUID.randomUUID(), type, List.of(m.id(), other.id()), null));
            placed.add(m.id());
            placed.add(other.id());
        }

        // 2) Individuales para quien prefiere SINGLE
        for (TravelPartyMember m : members) {
            if (placed.contains(m.id())) continue;
            if (m.roomingPreference().preferredRoomType() != RoomType.SINGLE) continue;
            if (remaining.getOrDefault(RoomType.SINGLE, 0) <= 0) continue;
            remaining.merge(RoomType.SINGLE, -1, Integer::sum);
            result.add(new RoomAssignment(UUID.randomUUID(), RoomType.SINGLE, List.of(m.id()), null));
            placed.add(m.id());
        }

        // 3) Emparejar al resto respetando doNotShareWith; si no hay pareja compatible, individual
        Deque<TravelPartyMember> pending = new ArrayDeque<>();
        for (TravelPartyMember m : members) {
            if (!placed.contains(m.id())) pending.add(m);
        }
        while (!pending.isEmpty()) {
            TravelPartyMember first = pending.poll();
            TravelPartyMember partner = findCompatiblePartner(first, pending);
            if (partner != null) {
                pending.remove(partner);
                RoomType type = allocate(remaining, RoomType.DOUBLE, RoomType.TWIN);
                result.add(new RoomAssignment(UUID.randomUUID(), type, List.of(first.id(), partner.id()), null));
            } else {
                RoomType type = allocate(remaining, RoomType.SINGLE);
                result.add(new RoomAssignment(UUID.randomUUID(), type, List.of(first.id()), null));
            }
        }

        return result;
    }

    private static TravelPartyMember findCompatiblePartner(TravelPartyMember first, Deque<TravelPartyMember> pool) {
        for (TravelPartyMember candidate : pool) {
            boolean forbidden = first.roomingPreference().doNotShareWith().contains(candidate.id())
                    || candidate.roomingPreference().doNotShareWith().contains(first.id());
            if (!forbidden) return candidate;
        }
        return null;
    }

    private static RoomType allocate(Map<RoomType, Integer> remaining, RoomType... preferenceOrder) {
        for (RoomType type : preferenceOrder) {
            if (remaining.getOrDefault(type, 0) > 0) {
                remaining.merge(type, -1, Integer::sum);
                return type;
            }
        }
        throw new InsufficientRoomAllotmentException(
                "Not enough rooms of type " + Arrays.toString(preferenceOrder) + " to complete the rooming list");
    }
}
