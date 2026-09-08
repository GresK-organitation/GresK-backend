-- ─────────────────────────────────────────────────────────────────────────
-- V9: agenda_entries
--
-- Agenda personal de la promotora: tareas, citas y recordatorios.
-- Soporta:
--   · Tres tipos de entrada: TASK | APPOINTMENT | REMINDER
--   · Recurrencia (diaria / semanal / mensual) con gestión de excepciones
--   · Vínculo opcional a entidades GresK (EVENT | ARTIST | VENUE)
--   · Notificación por email configurable
--   · Colores y etiquetas libres
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE agenda_entries (
    id                      UUID          NOT NULL,
    promoter_id             UUID          NOT NULL,
    type                    VARCHAR(20)   NOT NULL,

    title                   VARCHAR(255)  NOT NULL,
    description             TEXT,

    -- Temporalidad
    start_at                TIMESTAMPTZ,
    end_at                  TIMESTAMPTZ,
    all_day                 BOOLEAN       NOT NULL DEFAULT false,

    -- Estado
    completed               BOOLEAN       NOT NULL DEFAULT false,
    is_cancelled            BOOLEAN       NOT NULL DEFAULT false,

    -- Personalización visual
    color                   VARCHAR(7),
    label                   VARCHAR(100),

    -- Vínculo opcional a entidad GresK
    linked_entity_type      VARCHAR(20),
    linked_entity_id        UUID,

    -- Recurrencia (solo en entradas maestras)
    recurrence_frequency    VARCHAR(10),
    recurrence_interval     INTEGER,
    recurrence_count        INTEGER,
    recurrence_until        TIMESTAMPTZ,
    recurrence_by_day       VARCHAR(50),
    recurrence_by_month_day INTEGER,

    -- Gestión de series y excepciones
    -- series_id IS NOT NULL  → esta entrada es una excepción de la serie
    -- exception_date         → fecha de la ocurrencia original que reemplaza
    series_id               UUID,
    exception_date          TIMESTAMPTZ,

    -- Recordatorio por email
    reminder_minutes_before INTEGER,
    reminder_sent           BOOLEAN       NOT NULL DEFAULT false,

    created_at              TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_agenda_entries    PRIMARY KEY (id),
    CONSTRAINT fk_agenda_series     FOREIGN KEY (series_id) REFERENCES agenda_entries(id) ON DELETE CASCADE,
    CONSTRAINT chk_agenda_type      CHECK (type IN ('TASK', 'APPOINTMENT', 'REMINDER')),
    CONSTRAINT chk_agenda_freq      CHECK (recurrence_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY')
                                           OR recurrence_frequency IS NULL),
    CONSTRAINT chk_linked_type      CHECK (linked_entity_type IN ('EVENT', 'ARTIST', 'VENUE')
                                           OR linked_entity_type IS NULL)
);

-- Índice principal para la vista de agenda (rango de fechas por promotora)
CREATE INDEX idx_agenda_promoter_date ON agenda_entries (promoter_id, start_at);

-- Índice para cargar excepciones de una serie
CREATE INDEX idx_agenda_series ON agenda_entries (series_id)
    WHERE series_id IS NOT NULL;

-- Índice parcial para el scheduler de recordatorios
CREATE INDEX idx_agenda_reminders ON agenda_entries (start_at)
    WHERE reminder_minutes_before IS NOT NULL
      AND reminder_sent = false;
