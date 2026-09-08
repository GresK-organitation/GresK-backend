-- ─────────────────────────────────────────────────────────────────────────
-- V34: restructure rider module
--
-- TechnicalRider and HospitalityRider become independent aggregates.
-- SoundSystemRequirements/BacklineItem/InputChannel/HospitalityRequirements/
-- TransportRequirements are superseded by a single reusable RiderLineItem
-- concept (technical_rider_line_items / hospitality_rider_line_items),
-- carrying fulfillment (VENUE_STOCK/RENTAL_EXTERNAL/ARTIST_PROVIDED) and
-- equipment-substitution state.
--
-- Existing data in the dropped columns/tables is NOT migrated (explicit
-- decision — this schema was still in active development, no data worth
-- preserving).
-- ─────────────────────────────────────────────────────────────────────────

-- ── technical_riders: drop flattened sound system / hospitality / transport ─
ALTER TABLE technical_riders
    DROP COLUMN IF EXISTS console_brand,
    DROP COLUMN IF EXISTS console_channels,
    DROP COLUMN IF EXISTS monitor_mixes,
    DROP COLUMN IF EXISTS pa_description,
    DROP COLUMN IF EXISTS processor_notes,
    DROP COLUMN IF EXISTS dressing_room_capacity,
    DROP COLUMN IF EXISTS catering_notes,
    DROP COLUMN IF EXISTS water_bottles_on_stage,
    DROP COLUMN IF EXISTS passes_count,
    DROP COLUMN IF EXISTS vehicle_type,
    DROP COLUMN IF EXISTS passenger_capacity,
    DROP COLUMN IF EXISTS transport_notes;

DROP TABLE IF EXISTS rider_backline_items;
DROP TABLE IF EXISTS rider_input_channels;

-- ── technical_rider_line_items ───────────────────────────────────────────
CREATE TABLE technical_rider_line_items (
    id                          UUID         NOT NULL,
    rider_id                    UUID         NOT NULL,
    category                    VARCHAR(20)  NOT NULL,
    description                 VARCHAR(255) NOT NULL,
    quantity                    INTEGER      NOT NULL DEFAULT 1,
    required                    BOOLEAN      NOT NULL DEFAULT true,
    attributes                  JSONB,
    fulfillment_source          VARCHAR(20)  NOT NULL DEFAULT 'UNRESOLVED',
    equiv_requested_spec        VARCHAR(255),
    equiv_proposed_alternative  VARCHAR(255),
    equiv_status                VARCHAR(20),
    equiv_proposed_by           VARCHAR(20),
    equiv_notes                 TEXT,
    equiv_proposed_at           TIMESTAMPTZ,
    equiv_decided_at            TIMESTAMPTZ,
    notes                       TEXT,

    CONSTRAINT pk_technical_rider_line_items PRIMARY KEY (id),
    CONSTRAINT fk_technical_line_items_rider FOREIGN KEY (rider_id) REFERENCES technical_riders (id) ON DELETE CASCADE,
    CONSTRAINT chk_technical_line_items_category CHECK (category IN (
        'SOUND_PA','MICROPHONE','BACKLINE','LIGHTING','STAGE',
        'CATERING','DRESSING_ROOM','DIET','ACCOMMODATION','TRANSPORT','OTHER'
    )),
    CONSTRAINT chk_technical_line_items_fulfillment CHECK (fulfillment_source IN (
        'UNRESOLVED','VENUE_STOCK','RENTAL_EXTERNAL','ARTIST_PROVIDED'
    ))
);

CREATE INDEX idx_technical_line_items_rider_id ON technical_rider_line_items (rider_id);

-- ── hospitality_riders ────────────────────────────────────────────────────
CREATE TABLE hospitality_riders (
    id               UUID         NOT NULL,
    artist_id        UUID         NOT NULL,
    promoter_id      UUID         NOT NULL,
    name             VARCHAR(255) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    version          INTEGER      NOT NULL DEFAULT 1,
    additional_notes TEXT,
    share_token      VARCHAR(36)  UNIQUE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_hospitality_riders  PRIMARY KEY (id),
    CONSTRAINT fk_hospitality_riders_artist   FOREIGN KEY (artist_id)   REFERENCES artists   (id) ON DELETE CASCADE,
    CONSTRAINT fk_hospitality_riders_promoter FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE,
    CONSTRAINT chk_hospitality_riders_status  CHECK (status IN ('DRAFT', 'PUBLISHED'))
);

CREATE INDEX idx_hospitality_riders_artist_id   ON hospitality_riders (artist_id);
CREATE INDEX idx_hospitality_riders_promoter_id ON hospitality_riders (promoter_id);

-- ── hospitality_rider_line_items ─────────────────────────────────────────
CREATE TABLE hospitality_rider_line_items (
    id                          UUID         NOT NULL,
    rider_id                    UUID         NOT NULL,
    category                    VARCHAR(20)  NOT NULL,
    description                 VARCHAR(255) NOT NULL,
    quantity                    INTEGER      NOT NULL DEFAULT 1,
    required                    BOOLEAN      NOT NULL DEFAULT true,
    attributes                  JSONB,
    fulfillment_source          VARCHAR(20)  NOT NULL DEFAULT 'UNRESOLVED',
    equiv_requested_spec        VARCHAR(255),
    equiv_proposed_alternative  VARCHAR(255),
    equiv_status                VARCHAR(20),
    equiv_proposed_by           VARCHAR(20),
    equiv_notes                 TEXT,
    equiv_proposed_at           TIMESTAMPTZ,
    equiv_decided_at            TIMESTAMPTZ,
    notes                       TEXT,

    CONSTRAINT pk_hospitality_rider_line_items PRIMARY KEY (id),
    CONSTRAINT fk_hospitality_line_items_rider FOREIGN KEY (rider_id) REFERENCES hospitality_riders (id) ON DELETE CASCADE,
    CONSTRAINT chk_hospitality_line_items_category CHECK (category IN (
        'SOUND_PA','MICROPHONE','BACKLINE','LIGHTING','STAGE',
        'CATERING','DRESSING_ROOM','DIET','ACCOMMODATION','TRANSPORT','OTHER'
    )),
    CONSTRAINT chk_hospitality_line_items_fulfillment CHECK (fulfillment_source IN (
        'UNRESOLVED','VENUE_STOCK','RENTAL_EXTERNAL','ARTIST_PROVIDED'
    ))
);

CREATE INDEX idx_hospitality_line_items_rider_id ON hospitality_rider_line_items (rider_id);

-- ── checklist_entries: category now follows RiderItemCategory ───────────
ALTER TABLE checklist_entries DROP CONSTRAINT IF EXISTS chk_entry_category;
ALTER TABLE checklist_entries ADD CONSTRAINT chk_entry_category CHECK (category IN (
    'SOUND_PA','MICROPHONE','BACKLINE','LIGHTING','STAGE',
    'CATERING','DRESSING_ROOM','DIET','ACCOMMODATION','TRANSPORT','OTHER'
));
