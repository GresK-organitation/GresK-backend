CREATE TABLE bookings (
    id                       UUID PRIMARY KEY,
    promoter_id              UUID NOT NULL,
    artist_id                UUID NOT NULL,
    venue_id                 UUID,
    venue_name               VARCHAR(255) NOT NULL,
    venue_country            VARCHAR(100) NOT NULL,
    venue_region             VARCHAR(100),
    venue_city               VARCHAR(100) NOT NULL,
    venue_radius_km          DOUBLE PRECISION,
    event_date               TIMESTAMPTZ NOT NULL,
    status                   VARCHAR(20) NOT NULL,
    hold_expires_at          TIMESTAMPTZ,
    exclusivity_country      VARCHAR(100),
    exclusivity_region       VARCHAR(100),
    exclusivity_city         VARCHAR(100),
    exclusivity_radius_km    DOUBLE PRECISION,
    exclusivity_days_before  INTEGER,
    exclusivity_days_after   INTEGER,
    day_sheet_show_date      DATE,
    linked_event_id          UUID,
    linked_contract_id       UUID,
    notes                    TEXT,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_booking_status CHECK (status IN ('HOLD_1','HOLD_2','CONFIRMED','CANCELLED','EXPIRED'))
);

CREATE INDEX idx_bookings_promoter_date ON bookings (promoter_id, event_date);
CREATE INDEX idx_bookings_artist ON bookings (artist_id);
CREATE INDEX idx_bookings_expirable ON bookings (status, hold_expires_at) WHERE status IN ('HOLD_1','HOLD_2');
CREATE INDEX idx_bookings_venue ON bookings (venue_id) WHERE venue_id IS NOT NULL;

CREATE TABLE booking_milestones (
    id           UUID PRIMARY KEY,
    booking_id   UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    type         VARCHAR(30) NOT NULL,
    title        VARCHAR(255) NOT NULL,
    offset_days  INTEGER NOT NULL,
    due_date     TIMESTAMPTZ NOT NULL,
    status       VARCHAR(20) NOT NULL,
    completed_at TIMESTAMPTZ,
    notes        TEXT,
    CONSTRAINT chk_milestone_type CHECK (type IN ('PAYMENT','LICENSE','RIDER_SUBMISSION','MARKETING_CAMPAIGN','CUSTOM')),
    CONSTRAINT chk_milestone_status CHECK (status IN ('PENDING','COMPLETED','SKIPPED'))
);

CREATE INDEX idx_booking_milestones_booking ON booking_milestones (booking_id);
CREATE INDEX idx_booking_milestones_due ON booking_milestones (due_date) WHERE status = 'PENDING';

CREATE TABLE booking_day_sheet_entries (
    id          UUID PRIMARY KEY,
    booking_id  UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    entry_time  TIME NOT NULL,
    type        VARCHAR(20) NOT NULL,
    label       VARCHAR(255) NOT NULL,
    notes       TEXT,
    sort_order  INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_day_sheet_entries_booking ON booking_day_sheet_entries (booking_id);
