-- ─────────────────────────────────────────────────────────────────────────
-- V8: rider module
--
-- TechnicalRider  → plantilla por artista (aggregate root)
-- EventRiderChecklist → instancia de checklist por evento (aggregate root)
-- ChecklistEntry      → ítem individual del checklist (@Entity propio)
-- RiderAlert          → notificación in-app 72h (aggregate root)
--
-- RiderStatus: DRAFT, PUBLISHED
-- BacklineCategory: DRUMS, GUITARS, BASS, KEYBOARDS, VOCALS, SOUND,
--                   LIGHTING, HOSPITALITY, TRANSPORT, OTHER
-- StageElementType: DRUM_KIT, GUITAR_AMP, BASS_AMP, VOCAL_MIC, KEYBOARD,
--                   MONITOR, DI_BOX, STAGE_BOX, PIANO, CUSTOM
-- ─────────────────────────────────────────────────────────────────────────

-- ── technical_riders ─────────────────────────────────────────────────────
CREATE TABLE technical_riders (
    id              UUID         NOT NULL,
    artist_id       UUID         NOT NULL,
    promoter_id     UUID         NOT NULL,
    name            VARCHAR(255) NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    version         INTEGER      NOT NULL DEFAULT 1,

    -- soundcheck
    sound_check_duration_minutes  INTEGER,
    sound_check_notes             TEXT,

    -- sound system (SoundSystemRequirements flattened)
    console_brand        VARCHAR(100),
    console_channels     INTEGER,
    monitor_mixes        INTEGER,
    pa_description       TEXT,
    processor_notes      TEXT,

    -- stage dimensions (StageDimensions flattened)
    stage_width_meters      NUMERIC(6,2),
    stage_depth_meters      NUMERIC(6,2),
    stage_min_height_meters NUMERIC(6,2),
    power_outlets           INTEGER,
    has_drum_riser          BOOLEAN      NOT NULL DEFAULT false,

    -- stage plot: JSON array of StageElement {elementId, type, xPercent, yPercent, rotationDegrees, label}
    stage_elements          JSONB,

    -- hospitality (HospitalityRequirements flattened)
    dressing_room_capacity  INTEGER,
    catering_notes          TEXT,
    water_bottles_on_stage  INTEGER,
    passes_count            INTEGER,

    -- transport (TransportRequirements flattened)
    vehicle_type        VARCHAR(100),
    passenger_capacity  INTEGER,
    transport_notes     TEXT,

    additional_notes    TEXT,
    share_token         VARCHAR(36)  UNIQUE,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_technical_riders      PRIMARY KEY (id),
    CONSTRAINT fk_riders_artist         FOREIGN KEY (artist_id)   REFERENCES artists   (id) ON DELETE CASCADE,
    CONSTRAINT fk_riders_promoter       FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE,
    CONSTRAINT chk_riders_status        CHECK (status IN ('DRAFT', 'PUBLISHED'))
);

CREATE INDEX idx_riders_artist_id   ON technical_riders (artist_id);
CREATE INDEX idx_riders_promoter_id ON technical_riders (promoter_id);
CREATE INDEX idx_riders_status      ON technical_riders (status);

-- ── rider_staff (@ElementCollection) ─────────────────────────────────────
CREATE TABLE rider_staff (
    rider_id  UUID         NOT NULL,
    role      VARCHAR(100) NOT NULL,
    name      VARCHAR(100) NOT NULL,

    CONSTRAINT fk_rider_staff_rider FOREIGN KEY (rider_id) REFERENCES technical_riders (id) ON DELETE CASCADE
);

CREATE INDEX idx_rider_staff_rider_id ON rider_staff (rider_id);

-- ── rider_input_channels (@ElementCollection) ────────────────────────────
CREATE TABLE rider_input_channels (
    rider_id        UUID         NOT NULL,
    channel_number  INTEGER      NOT NULL,
    instrument      VARCHAR(150) NOT NULL,
    microphone      VARCHAR(150),
    inserts         VARCHAR(150),
    notes           TEXT,

    CONSTRAINT fk_rider_channels_rider FOREIGN KEY (rider_id) REFERENCES technical_riders (id) ON DELETE CASCADE
);

CREATE INDEX idx_rider_channels_rider_id ON rider_input_channels (rider_id);

-- ── rider_backline_items (@ElementCollection) ────────────────────────────
CREATE TABLE rider_backline_items (
    rider_id    UUID         NOT NULL,
    category    VARCHAR(20)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    brand       VARCHAR(100),
    model       VARCHAR(100),
    required    BOOLEAN      NOT NULL DEFAULT true,

    CONSTRAINT fk_rider_backline_rider    FOREIGN KEY (rider_id) REFERENCES technical_riders (id) ON DELETE CASCADE,
    CONSTRAINT chk_backline_category      CHECK (category IN (
        'DRUMS','GUITARS','BASS','KEYBOARDS','VOCALS',
        'SOUND','LIGHTING','HOSPITALITY','TRANSPORT','OTHER'
    ))
);

CREATE INDEX idx_rider_backline_rider_id ON rider_backline_items (rider_id);

-- ── event_rider_checklists ────────────────────────────────────────────────
CREATE TABLE event_rider_checklists (
    id             UUID        NOT NULL,
    event_id       UUID        NOT NULL UNIQUE,
    rider_id       UUID        NOT NULL,
    alert_sent_at  TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_event_rider_checklists  PRIMARY KEY (id),
    CONSTRAINT fk_checklist_event         FOREIGN KEY (event_id) REFERENCES events           (id) ON DELETE CASCADE,
    CONSTRAINT fk_checklist_rider         FOREIGN KEY (rider_id) REFERENCES technical_riders (id) ON DELETE RESTRICT
);

CREATE INDEX idx_checklists_event_id ON event_rider_checklists (event_id);
CREATE INDEX idx_checklists_rider_id ON event_rider_checklists (rider_id);

-- Candidates for the 72h alert scheduler
CREATE INDEX idx_checklists_alert_candidates
    ON event_rider_checklists (event_id)
   WHERE alert_sent_at IS NULL;

-- ── checklist_entries (@Entity with own PK) ───────────────────────────────
CREATE TABLE checklist_entries (
    entry_id         UUID         NOT NULL,
    checklist_id     UUID         NOT NULL,
    category         VARCHAR(20)  NOT NULL,
    description      VARCHAR(255) NOT NULL,
    required         BOOLEAN      NOT NULL DEFAULT true,
    confirmed        BOOLEAN      NOT NULL DEFAULT false,
    confirmed_at     TIMESTAMPTZ,
    confirmed_notes  TEXT,

    CONSTRAINT pk_checklist_entries        PRIMARY KEY (entry_id),
    CONSTRAINT fk_entries_checklist        FOREIGN KEY (checklist_id) REFERENCES event_rider_checklists (id) ON DELETE CASCADE,
    CONSTRAINT chk_entry_category          CHECK (category IN (
        'DRUMS','GUITARS','BASS','KEYBOARDS','VOCALS',
        'SOUND','LIGHTING','HOSPITALITY','TRANSPORT','OTHER'
    ))
);

CREATE INDEX idx_checklist_entries_checklist_id ON checklist_entries (checklist_id);

-- ── rider_alerts ──────────────────────────────────────────────────────────
CREATE TABLE rider_alerts (
    id           UUID  NOT NULL,
    promoter_id  UUID  NOT NULL,
    event_id     UUID  NOT NULL,
    rider_id     UUID  NOT NULL,
    message      TEXT  NOT NULL,
    read         BOOLEAN     NOT NULL DEFAULT false,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rider_alerts      PRIMARY KEY (id),
    CONSTRAINT fk_alerts_promoter   FOREIGN KEY (promoter_id) REFERENCES promoters        (id) ON DELETE CASCADE,
    CONSTRAINT fk_alerts_event      FOREIGN KEY (event_id)    REFERENCES events            (id) ON DELETE CASCADE,
    CONSTRAINT fk_alerts_rider      FOREIGN KEY (rider_id)    REFERENCES technical_riders  (id) ON DELETE CASCADE
);

CREATE INDEX idx_rider_alerts_promoter_id ON rider_alerts (promoter_id);
CREATE INDEX idx_rider_alerts_unread
    ON rider_alerts (promoter_id, created_at DESC)
   WHERE read = false;
