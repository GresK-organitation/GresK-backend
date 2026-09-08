package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.domain.exception.CrewMemberNotFoundException;
import com.gresk.modules.logistics.domain.exception.ForbiddenLogisticsOperationException;
import com.gresk.modules.logistics.domain.exception.ItineraryNotFoundException;
import com.gresk.modules.logistics.domain.exception.RoomingListNotFoundException;
import com.gresk.modules.logistics.domain.exception.TourNotFoundException;
import com.gresk.modules.logistics.domain.exception.TravelPartyNotFoundException;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.model.CrewMemberId;
import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.ItineraryId;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.RoomingListId;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.model.TravelPartyId;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.ItineraryRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.TravelPartyRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

/** Búsquedas repetidas (aggregate + verificación de que pertenece a la promotora) usadas por los use cases. */
final class LogisticsLookups {

    private LogisticsLookups() {}

    static Tour requireTour(TourRepositoryPort repository, String tourId, String promoterId) {
        Tour tour = repository.findById(TourId.of(tourId)).orElseThrow(() -> new TourNotFoundException(tourId));
        requireOwnership(tour.getPromoterId(), promoterId, "Tour");
        return tour;
    }

    static TravelParty requireTravelPartyById(TravelPartyRepositoryPort repository, String travelPartyId, String promoterId) {
        TravelParty party = repository.findById(TravelPartyId.of(travelPartyId))
                .orElseThrow(() -> new TravelPartyNotFoundException(travelPartyId));
        requireOwnership(party.getPromoterId(), promoterId, "TravelParty");
        return party;
    }

    static TravelParty requireTravelPartyByTour(TravelPartyRepositoryPort repository, TourId tourId, String promoterId) {
        TravelParty party = repository.findByTourId(tourId)
                .orElseThrow(() -> new TravelPartyNotFoundException("for tour " + tourId));
        requireOwnership(party.getPromoterId(), promoterId, "TravelParty");
        return party;
    }

    static CrewMember requireCrewMember(CrewMemberRepositoryPort repository, String crewMemberId, String promoterId) {
        CrewMember member = repository.findById(CrewMemberId.of(crewMemberId))
                .orElseThrow(() -> new CrewMemberNotFoundException(crewMemberId));
        requireOwnership(member.getPromoterId(), promoterId, "CrewMember");
        return member;
    }

    static Itinerary requireItineraryById(ItineraryRepositoryPort repository, String itineraryId, String promoterId) {
        Itinerary itinerary = repository.findById(ItineraryId.of(itineraryId))
                .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));
        requireOwnership(itinerary.getPromoterId(), promoterId, "Itinerary");
        return itinerary;
    }

    static Itinerary requireItineraryByTour(ItineraryRepositoryPort repository, TourId tourId, String promoterId) {
        Itinerary itinerary = repository.findByTourId(tourId)
                .orElseThrow(() -> new ItineraryNotFoundException("for tour " + tourId));
        requireOwnership(itinerary.getPromoterId(), promoterId, "Itinerary");
        return itinerary;
    }

    static RoomingList requireRoomingListById(RoomingListRepositoryPort repository, String roomingListId, String promoterId) {
        RoomingList roomingList = repository.findById(RoomingListId.of(roomingListId))
                .orElseThrow(() -> new RoomingListNotFoundException(roomingListId));
        requireOwnership(roomingList.getPromoterId(), promoterId, "RoomingList");
        return roomingList;
    }

    private static void requireOwnership(PromoterId owner, String promoterId, String resource) {
        if (!owner.equals(PromoterId.of(promoterId))) {
            throw new ForbiddenLogisticsOperationException(resource + " does not belong to promoter");
        }
    }
}
