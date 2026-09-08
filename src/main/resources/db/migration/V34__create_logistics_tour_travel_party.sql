-- ─────────────────────────────────────────────────────────────────────────
-- V34: módulo logistics — Tour, TravelParty, CrewMember
--
-- Tour: gira (una o varias fechas/Booking) de un artista, con sus legs
--   denormalizados, contactos de emergencia y puntos de interés para el Tour Book.
-- TravelParty: roster de expedición (músicos + crew) de un Tour, relación 1:1.
-- CrewMember: personal de gira contratado por la promotora sin vínculo a un
--   Artist (mismo shape de documentación de identidad que artist.band_member).
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE tours (
    id           UUID         NOT NULL,
    promoter_id  UUID         NOT NULL,
    artist_id    UUID         NOT NULL,
    name         VARCHAR(255) NOT NULL,
    start_date   DATE         NOT NULL,
    end_date     DATE         NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    notes        TEXT,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_tours PRIMARY KEY (id),
    CONSTRAINT chk_tour_status CHECK (status IN ('PLANNED','ACTIVE','COMPLETED','CANCELLED'))
);

CREATE INDEX idx_tours_promoter        ON tours (promoter_id);
CREATE INDEX idx_tours_promoter_status ON tours (promoter_id, status);
CREATE INDEX idx_tours_artist          ON tours (artist_id);

CREATE TABLE tour_legs (
    tour_id        UUID         NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    booking_id     UUID         NOT NULL,
    sequence_order INTEGER      NOT NULL,
    show_date      DATE         NOT NULL,
    venue_name     VARCHAR(255) NOT NULL,
    venue_city     VARCHAR(100)
);

CREATE INDEX idx_tour_legs_tour ON tour_legs (tour_id);

CREATE TABLE tour_emergency_contacts (
    tour_id       UUID         NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    contact_name  VARCHAR(255) NOT NULL,
    contact_role  VARCHAR(100),
    contact_phone VARCHAR(50)  NOT NULL,
    contact_notes TEXT
);

CREATE INDEX idx_tour_emergency_contacts_tour ON tour_emergency_contacts (tour_id);

CREATE TABLE tour_points_of_interest (
    tour_id       UUID         NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    poi_name      VARCHAR(255) NOT NULL,
    poi_category  VARCHAR(20)  NOT NULL,
    poi_address   VARCHAR(255),
    poi_latitude  DOUBLE PRECISION,
    poi_longitude DOUBLE PRECISION,
    poi_notes     TEXT,
    CONSTRAINT chk_poi_category CHECK (poi_category IN ('RESTAURANT','PHARMACY','HOSPITAL','LAUNDRY','OTHER'))
);

CREATE INDEX idx_tour_pois_tour ON tour_points_of_interest (tour_id);

CREATE TABLE travel_parties (
    id          UUID        NOT NULL,
    tour_id     UUID        NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    promoter_id UUID        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_travel_parties PRIMARY KEY (id),
    CONSTRAINT uq_travel_parties_tour UNIQUE (tour_id)
);

CREATE INDEX idx_travel_parties_promoter ON travel_parties (promoter_id);

CREATE TABLE travel_party_members (
    id                    UUID         NOT NULL,
    travel_party_id       UUID         NOT NULL REFERENCES travel_parties(id) ON DELETE CASCADE,
    person_type           VARCHAR(20)  NOT NULL,
    person_id             UUID         NOT NULL,
    display_name          VARCHAR(255) NOT NULL,
    role                  VARCHAR(30)  NOT NULL,
    active                BOOLEAN      NOT NULL DEFAULT true,
    preferred_room_type   VARCHAR(20),
    preferred_roommate_id UUID,

    CONSTRAINT pk_travel_party_members PRIMARY KEY (id),
    CONSTRAINT chk_travel_party_member_person_type CHECK (person_type IN ('BAND_MEMBER','CREW')),
    CONSTRAINT chk_travel_party_member_room_type CHECK (preferred_room_type IS NULL OR preferred_room_type IN ('SINGLE','DOUBLE','TWIN'))
);

CREATE INDEX idx_travel_party_members_party ON travel_party_members (travel_party_id);

CREATE TABLE travel_party_member_do_not_share (
    member_id       UUID NOT NULL REFERENCES travel_party_members(id) ON DELETE CASCADE,
    other_member_id UUID NOT NULL
);

CREATE INDEX idx_tpm_do_not_share_member ON travel_party_member_do_not_share (member_id);

CREATE TABLE travel_party_member_needs (
    member_id        UUID        NOT NULL REFERENCES travel_party_members(id) ON DELETE CASCADE,
    need_type        VARCHAR(20) NOT NULL,
    need_description TEXT        NOT NULL,
    CONSTRAINT chk_need_type CHECK (need_type IN ('DIETARY','MOBILITY','MEDICAL','DOCUMENT','OTHER'))
);

CREATE INDEX idx_tpm_needs_member ON travel_party_member_needs (member_id);

CREATE TABLE crew_members (
    id            UUID         NOT NULL,
    promoter_id   UUID         NOT NULL,
    name          VARCHAR(100) NOT NULL,
    default_role  VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(50),
    contact_email VARCHAR(255),
    active        BOOLEAN      NOT NULL DEFAULT true,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_crew_members PRIMARY KEY (id)
);

CREATE INDEX idx_crew_members_promoter ON crew_members (promoter_id);
CREATE INDEX idx_crew_members_active   ON crew_members (active) WHERE active = true;

CREATE TABLE crew_member_document (
    crew_member_id  UUID         NOT NULL REFERENCES crew_members(id) ON DELETE CASCADE,
    doc_type        VARCHAR(20)  NOT NULL,
    document_number VARCHAR(100) NOT NULL,
    issuing_country VARCHAR(100) NOT NULL,
    expiry_date     DATE,
    CONSTRAINT chk_crew_doc_type CHECK (doc_type IN ('NATIONAL_ID','PASSPORT','WORK_VISA','RESIDENCE_PERMIT'))
);

CREATE INDEX idx_crew_member_document_member ON crew_member_document (crew_member_id);
