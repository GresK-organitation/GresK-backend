CREATE TABLE calendar_sync_accounts (
    id                        UUID PRIMARY KEY,
    promoter_id               UUID NOT NULL,
    provider                  VARCHAR(20) NOT NULL,
    external_account_email    VARCHAR(300),
    access_token_ciphertext   TEXT,
    refresh_token_ciphertext  TEXT,
    token_expiry              TIMESTAMPTZ,
    status                    VARCHAR(20) NOT NULL,
    sync_token                TEXT,
    last_synced_at            TIMESTAMPTZ,
    last_error                TEXT,
    watch_channel_id          VARCHAR(255),
    watch_resource_id         VARCHAR(255),
    watch_expiry               TIMESTAMPTZ,
    ms_subscription_id         VARCHAR(255),
    ms_subscription_expiry     TIMESTAMPTZ,
    client_state_secret        VARCHAR(255) NOT NULL,
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_calendar_provider CHECK (provider IN ('GOOGLE','OUTLOOK')),
    CONSTRAINT chk_calendar_status CHECK (status IN ('CONNECTED','DISCONNECTED','ERROR','TOKEN_EXPIRED'))
);

-- Un único proveedor CONECTADO por promotor a la vez, forzado también a nivel de BD
CREATE UNIQUE INDEX uq_calendar_sync_connected ON calendar_sync_accounts (promoter_id, provider) WHERE status = 'CONNECTED';
CREATE INDEX idx_calendar_sync_watch ON calendar_sync_accounts (watch_channel_id) WHERE watch_channel_id IS NOT NULL;
CREATE INDEX idx_calendar_sync_ms_sub ON calendar_sync_accounts (ms_subscription_id) WHERE ms_subscription_id IS NOT NULL;

CREATE TABLE calendar_event_mappings (
    id                 UUID PRIMARY KEY,
    sync_account_id    UUID NOT NULL REFERENCES calendar_sync_accounts(id) ON DELETE CASCADE,
    local_entry_id     UUID NOT NULL,
    local_type         VARCHAR(20) NOT NULL,
    external_event_id  VARCHAR(255) NOT NULL,
    external_etag      VARCHAR(255),
    last_pushed_at     TIMESTAMPTZ,
    last_pulled_at     TIMESTAMPTZ,
    CONSTRAINT chk_local_type CHECK (local_type IN ('AGENDA_ENTRY','BOOKING')),
    CONSTRAINT uq_mapping_local UNIQUE (sync_account_id, local_entry_id, local_type)
);

CREATE INDEX idx_calendar_mappings_external ON calendar_event_mappings (sync_account_id, external_event_id);
