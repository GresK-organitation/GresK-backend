-- ─────────────────────────────────────────────────────────────────────────
-- V35: módulo logistics — Itinerary, RoomingList
--
-- Itinerary: tramos de transporte (vuelo/tren/transfer) de un Tour, relación 1:1.
-- RoomingList: bloque de hotel de un Tour (cupos por tipo) y su reparto de
--   habitaciones, generado por RoomingListGenerator o editado a mano.
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE itineraries (
    id          UUID        NOT NULL,
    tour_id     UUID        NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    promoter_id UUID        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_itineraries PRIMARY KEY (id),
    CONSTRAINT uq_itineraries_tour UNIQUE (tour_id)
);

CREATE INDEX idx_itineraries_promoter ON itineraries (promoter_id);

CREATE TABLE itinerary_segments (
    id                     UUID         NOT NULL,
    itinerary_id           UUID         NOT NULL REFERENCES itineraries(id) ON DELETE CASCADE,
    type                   VARCHAR(20)  NOT NULL,
    departure_at           TIMESTAMPTZ  NOT NULL,
    departure_location     VARCHAR(255) NOT NULL,
    arrival_at             TIMESTAMPTZ,
    arrival_location       VARCHAR(255) NOT NULL,
    carrier_or_operator    VARCHAR(255),
    segment_code           VARCHAR(50),
    confirmation_reference VARCHAR(100),
    seat_or_capacity_info  VARCHAR(255),
    voucher_url            VARCHAR(500),
    notes                  TEXT,

    CONSTRAINT pk_itinerary_segments PRIMARY KEY (id),
    CONSTRAINT chk_segment_type CHECK (type IN ('FLIGHT','TRAIN','GROUND_TRANSPORT'))
);

CREATE INDEX idx_itinerary_segments_itinerary ON itinerary_segments (itinerary_id);
CREATE INDEX idx_itinerary_segments_departure ON itinerary_segments (departure_at);

CREATE TABLE itinerary_segment_travelers (
    segment_id  UUID NOT NULL REFERENCES itinerary_segments(id) ON DELETE CASCADE,
    traveler_id UUID NOT NULL
);

CREATE INDEX idx_itinerary_segment_travelers_segment ON itinerary_segment_travelers (segment_id);

CREATE TABLE rooming_lists (
    id             UUID         NOT NULL,
    tour_id        UUID         NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    promoter_id    UUID         NOT NULL,
    hotel_name     VARCHAR(255) NOT NULL,
    hotel_address  VARCHAR(255),
    check_in_date  DATE         NOT NULL,
    check_out_date DATE         NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rooming_lists PRIMARY KEY (id)
);

CREATE INDEX idx_rooming_lists_tour     ON rooming_lists (tour_id);
CREATE INDEX idx_rooming_lists_promoter ON rooming_lists (promoter_id);

CREATE TABLE rooming_list_allotments (
    rooming_list_id UUID          NOT NULL REFERENCES rooming_lists(id) ON DELETE CASCADE,
    room_type       VARCHAR(20)   NOT NULL,
    quantity        INTEGER       NOT NULL,
    cost_amount     NUMERIC(12,2) NOT NULL,
    cost_currency   VARCHAR(3)    NOT NULL,
    CONSTRAINT chk_allotment_room_type CHECK (room_type IN ('SINGLE','DOUBLE','TWIN'))
);

CREATE INDEX idx_rooming_list_allotments_list ON rooming_list_allotments (rooming_list_id);

CREATE TABLE room_assignments (
    id              UUID        NOT NULL,
    rooming_list_id UUID        NOT NULL REFERENCES rooming_lists(id) ON DELETE CASCADE,
    room_type       VARCHAR(20) NOT NULL,
    room_number     VARCHAR(20),

    CONSTRAINT pk_room_assignments PRIMARY KEY (id),
    CONSTRAINT chk_assignment_room_type CHECK (room_type IN ('SINGLE','DOUBLE','TWIN'))
);

CREATE INDEX idx_room_assignments_list ON room_assignments (rooming_list_id);

CREATE TABLE room_assignment_occupants (
    assignment_id  UUID    NOT NULL REFERENCES room_assignments(id) ON DELETE CASCADE,
    occupant_id    UUID    NOT NULL,
    occupant_order INTEGER NOT NULL
);

CREATE INDEX idx_room_assignment_occupants_assignment ON room_assignment_occupants (assignment_id);
