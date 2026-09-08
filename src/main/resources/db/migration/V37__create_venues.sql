-- ─────────────────────────────────────────────────────────────────────────
-- V37: venues (ficha técnica de recintos)
--
-- Venue aggregate:
--   id, owner_id (FK→promoters), name, address (street/city/country),
--   capacity_configurations (JSONB: lista de CapacityConfiguration),
--   curfew_* (CurfewPolicy, nullable como bloque),
--   evac_* (EvacuationPlan, nullable como bloque),
--   licenses (JSONB: lista de MunicipalLicense),
--   dock_* (LoadingDockSpec, nullable como bloque),
--   active, created_at, updated_at
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE venues (
    id                  UUID          NOT NULL,
    owner_id            UUID          NOT NULL,
    name                VARCHAR(255)  NOT NULL,

    -- dirección
    address_street      VARCHAR(255)  NOT NULL,
    address_city        VARCHAR(100)  NOT NULL,
    address_country     VARCHAR(100)  NOT NULL,

    -- aforos modulares: [{code,label,layout,maxCapacity}]
    capacity_configurations JSONB     NOT NULL DEFAULT '[]'::jsonb,

    -- curfew / restricción de ruido (bloque nullable)
    curfew_hard_cutoff       TIME,
    curfew_max_decibels      INTEGER,
    curfew_restricted_days   JSONB,             -- lista de DayOfWeek, null = todos los días
    curfew_notes             TEXT,

    -- plano de evacuación (bloque nullable)
    evac_document_asset_id   VARCHAR(512),
    evac_certified_capacity  INTEGER,
    evac_last_reviewed_at    DATE,
    evac_reviewed_by         VARCHAR(255),

    -- licencias municipales: [{licenseNumber,type,issuingAuthority,validFrom,validUntil}]
    licenses                 JSONB     NOT NULL DEFAULT '[]'::jsonb,

    -- carga/descarga (bloque nullable)
    dock_access_height_m     NUMERIC(6,2),
    dock_access_width_m      NUMERIC(6,2),
    dock_max_vehicle_kg      NUMERIC(10,2),
    dock_window_start        TIME,
    dock_window_end          TIME,
    dock_count               INTEGER,
    dock_notes               TEXT,

    active                   BOOLEAN       NOT NULL DEFAULT true,
    created_at               TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_venues         PRIMARY KEY (id),
    CONSTRAINT fk_venues_owner   FOREIGN KEY (owner_id) REFERENCES promoters (id) ON DELETE RESTRICT
);

CREATE INDEX idx_venues_owner_id ON venues (owner_id);
